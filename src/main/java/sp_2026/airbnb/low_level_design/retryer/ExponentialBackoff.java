package sp_2026.airbnb.low_level_design.retryer;

public class ExponentialBackoff implements BackoffStrategy {
    private final long baseMs;
    private final long capMs;

    public ExponentialBackoff(long baseMs, long capMs) {
        this.baseMs = baseMs;
        this.capMs = capMs;
    }

    @Override
    public long delayMs(int attempt) {
        long delay = baseMs * (1L << attempt);
        return Math.min(delay, capMs);
    }
}
