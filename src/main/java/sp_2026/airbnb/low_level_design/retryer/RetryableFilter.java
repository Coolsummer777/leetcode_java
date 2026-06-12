package sp_2026.airbnb.low_level_design.retryer;

public interface RetryableFilter {
    boolean shouldRetry(Exception exception, int attempt);
}
