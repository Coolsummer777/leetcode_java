package sp_2026.airbnb.low_level_design.retryer;

import java.util.concurrent.Callable;

/**
 *
 * Implement Retryer
 * Implement a `Retryer` that wraps a callable and retries on failure under a configurable policy
 * (max attempts, backoff strategy, retryable exception filter). Discussion centers on OOD: how to make
 * the retry strategy pluggable.
 *
 * Requirements
 * Provide a default Retryer(max_attempts, backoff) that:
 * Invokes the wrapped callable.
 * On exception, sleeps according to backoff, increments the attempt counter, and retries until
 * max_attempts is hit.
 * Re-raises the last exception after exhausting retries.
 * Make the design extensible to:
 * Different backoff strategies (fixed, linear, exponential, exponential with jitter).
 * A filter that decides whether a given exception is retryable.
 * A circuit-breaker that gives up after consecutive failures across many calls.
 * A pre- / post-attempt hook (for logging or metrics).
 *
 * Notes
 * Pull BackoffStrategy, RetryableFilter, and Hook out into protocols / abstract classes; the Retryer
 * composes them.
 * Exponential backoff with jitter: delay = min(cap, base * 2**attempt) * (0.5 + random()) — the jitter
 * prevents thundering-herd retry storms.
 * Idempotency caveat: retrying a non-idempotent operation (e.g. POST /charge) requires an idempotency key
 * the caller must provide; surface this in the API or refuse to wrap.
 * Async variant: support both sync and async callables — separate Retryer and AsyncRetryer, or branch on
 * inspect.iscoroutinefunction(fn).
 * The interviewer grades on the extensibility discussion as much as the code: walk through the "open / closed"
 * principle and how a new backoff strategy plugs in without touching Retryer.
 *
 * Preparation
 * Implement the sync Retryer with pluggable backoff and a retryable-exception filter in under 25 minutes.
 * Layer on a Hook interface for on_attempt / on_failure / on_success.
 * Pre-write the async variant on paper so you can sketch it quickly if asked.
 * Be ready to discuss two specific edge cases: (1) catching KeyboardInterrupt (do not), (2) retrying inside
 * a with statement that owns a non-idempotent resource.
 *
 * Implementation (current):
 * - Pluggable BackoffStrategy (Fixed, Exponential, ExponentialWithJitter), RetryableFilter, Hook,
 *   CircuitBreaker.
 * - maxAttempts = total call count; attempt is 0-based for backoff/filter.
 * - Not yet: async variant.
 *
 */
public class Retryer {
    private final int maxAttempts;
    private final BackoffStrategy backoff;
    private final RetryableFilter filter;
    private final Hook hook;
    private final CircuitBreaker circuitBreaker;

    public Retryer(int maxAttempts, BackoffStrategy backoff) {
        this(maxAttempts, backoff, new DefaultRetryableFilter(), new NoOpHook(), new NoOpCircuitBreaker());
    }

    public Retryer(int maxAttempts, BackoffStrategy backoff, RetryableFilter filter, Hook hook) {
        this(maxAttempts, backoff, filter, hook, new NoOpCircuitBreaker());
    }

    public Retryer(
            int maxAttempts,
            BackoffStrategy backoff,
            RetryableFilter filter,
            Hook hook,
            CircuitBreaker circuitBreaker) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1");
        }
        this.maxAttempts = maxAttempts;
        this.backoff = backoff;
        this.filter = filter;
        this.hook = hook;
        this.circuitBreaker = circuitBreaker;
    }

    public <T> T call(Callable<T> callable) throws Exception {
        Exception lastException = null;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            if (!circuitBreaker.allowRequest()) {
                throw new CircuitBreakerOpenException();
            }

            hook.onAttempt(attempt);
            try {
                T result = callable.call();
                hook.onSuccess(attempt);
                circuitBreaker.recordSuccess();
                return result;
            } catch (Exception e) {
                lastException = e;
                hook.onFailure(attempt, e);
                circuitBreaker.recordFailure();

                boolean isLastAttempt = attempt == maxAttempts - 1;
                if (isLastAttempt || !filter.shouldRetry(e, attempt)) {
                    throw e;
                }

                sleep(backoff.delayMs(attempt));
            }
        }

        throw lastException;
    }

    private void sleep(long delayMs) throws InterruptedException {
        if (delayMs > 0) {
            Thread.sleep(delayMs);
        }
    }
}
