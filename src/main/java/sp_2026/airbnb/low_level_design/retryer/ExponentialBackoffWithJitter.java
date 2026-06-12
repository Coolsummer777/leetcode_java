package sp_2026.airbnb.low_level_design.retryer;

import java.util.Random;

public class ExponentialBackoffWithJitter implements BackoffStrategy {
    private final long baseMs;
    private final long capMs;
    private final Random random;

    public ExponentialBackoffWithJitter(long baseMs, long capMs) {
        this.baseMs = baseMs;
        this.capMs = capMs;
        this.random = new Random();
    }

    @Override
    public long delayMs(int attempt) {
        long exponential = Math.min(baseMs * (1L << attempt), capMs);
        double jitterFactor = 0.5 + random.nextDouble();
        return (long) (exponential * jitterFactor);
    }
}
