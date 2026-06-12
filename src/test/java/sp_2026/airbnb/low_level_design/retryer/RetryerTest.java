package sp_2026.airbnb.low_level_design.retryer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class RetryerTest {

    @Test
    void succeedsOnFirstAttempt() throws Exception {
        Retryer retryer = new Retryer(3, new FixedBackoff(0));
        assertEquals("ok", retryer.call(() -> "ok"));
    }

    @Test
    void retriesUntilSuccess() throws Exception {
        Retryer retryer = new Retryer(3, new FixedBackoff(0));
        AtomicInteger attempts = new AtomicInteger();

        String result = retryer.call(() -> {
            if (attempts.incrementAndGet() < 3) {
                throw new RuntimeException("fail");
            }
            return "ok";
        });

        assertEquals("ok", result);
        assertEquals(3, attempts.get());
    }

    @Test
    void throwsLastExceptionWhenExhausted() {
        Retryer retryer = new Retryer(2, new FixedBackoff(0));
        AtomicInteger attempts = new AtomicInteger();

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                retryer.call(() -> {
                    attempts.incrementAndGet();
                    throw new RuntimeException("fail");
                }));

        assertEquals("fail", thrown.getMessage());
        assertEquals(2, attempts.get());
    }

    @Test
    void filterSkipsRetryForNonRetryableException() {
        RetryableFilter ioOnly = (e, attempt) -> e instanceof IOException;
        Retryer retryer = new Retryer(3, new FixedBackoff(0), ioOnly, new NoOpHook());
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(RuntimeException.class, () ->
                retryer.call(() -> {
                    attempts.incrementAndGet();
                    throw new RuntimeException("not retryable");
                }));

        assertEquals(1, attempts.get());
    }

    @Test
    void doesNotRetryInterruptedException() {
        Retryer retryer = new Retryer(3, new FixedBackoff(0));
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(InterruptedException.class, () ->
                retryer.call(() -> {
                    attempts.incrementAndGet();
                    throw new InterruptedException();
                }));

        assertEquals(1, attempts.get());
    }

    @Test
    void jitterBackoffStaysWithinExpectedRange() {
        ExponentialBackoffWithJitter backoff = new ExponentialBackoffWithJitter(100, 1000);
        for (int i = 0; i < 20; i++) {
            long delay = backoff.delayMs(1); // base * 2 = 200, cap 1000 → jitter in [100, 300]
            assertTrue(delay >= 100 && delay <= 300);
        }
    }

    @Test
    void circuitBreakerOpensAfterConsecutiveFailures() throws Exception {
        ConsecutiveFailureCircuitBreaker breaker = new ConsecutiveFailureCircuitBreaker(2, 60_000);
        Retryer retryer = new Retryer(1, new FixedBackoff(0), new DefaultRetryableFilter(), new NoOpHook(), breaker);
        AtomicInteger attempts = new AtomicInteger();

        Callable<Void> failing = () -> {
            attempts.incrementAndGet();
            throw new RuntimeException("fail");
        };

        assertThrows(RuntimeException.class, () -> retryer.call(failing));
        assertThrows(RuntimeException.class, () -> retryer.call(failing));
        assertEquals(2, attempts.get());

        attempts.set(0);
        assertThrows(CircuitBreakerOpenException.class, () -> retryer.call(failing));
        assertEquals(0, attempts.get());
    }

    @Test
    void circuitBreakerClosesAfterSuccess() throws Exception {
        ConsecutiveFailureCircuitBreaker breaker = new ConsecutiveFailureCircuitBreaker(2, 60_000);
        Retryer retryer = new Retryer(1, new FixedBackoff(0), new DefaultRetryableFilter(), new NoOpHook(), breaker);

        assertThrows(RuntimeException.class, () -> retryer.call(() -> {
            throw new RuntimeException("fail");
        }));
        assertEquals("ok", retryer.call(() -> "ok"));
        assertEquals("ok again", retryer.call(() -> "ok again"));
    }
}
