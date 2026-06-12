package sp_2026.airbnb.low_level_design.retryer;

public interface BackoffStrategy {
    /** Delay before the next attempt; {@code attempt} is 0-based (0 = before 2nd call). */
    long delayMs(int attempt);
}
