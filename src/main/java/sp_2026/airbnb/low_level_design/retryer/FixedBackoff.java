package sp_2026.airbnb.low_level_design.retryer;

public class FixedBackoff implements BackoffStrategy {
    private final long delayMs;

    public FixedBackoff(long delayMs) {
        this.delayMs = delayMs;
    }

    @Override
    public long delayMs(int attempt) {
        return delayMs;
    }
}
