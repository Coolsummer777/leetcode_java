package sp_2026.airbnb.low_level_design.retryer;

public class DefaultRetryableFilter implements RetryableFilter {
    @Override
    public boolean shouldRetry(Exception exception, int attempt) {
        return !(exception instanceof InterruptedException);
    }
}
