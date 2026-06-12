package sp_2026.airbnb.low_level_design.retryer;

public class NoOpCircuitBreaker implements CircuitBreaker {
    @Override
    public boolean allowRequest() {
        return true;
    }

    @Override
    public void recordSuccess() {}

    @Override
    public void recordFailure() {}
}
