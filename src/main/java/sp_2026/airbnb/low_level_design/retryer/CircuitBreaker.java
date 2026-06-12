package sp_2026.airbnb.low_level_design.retryer;

public interface CircuitBreaker {
    boolean allowRequest();

    void recordSuccess();

    void recordFailure();
}
