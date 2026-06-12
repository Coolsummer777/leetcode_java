package sp_2026.airbnb.low_level_design.retryer;

public class CircuitBreakerOpenException extends RuntimeException {
    public CircuitBreakerOpenException() {
        super("Circuit breaker is open");
    }
}
