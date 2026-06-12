package sp_2026.airbnb.low_level_design.retryer;

/**
 * Opens after {@code failureThreshold} consecutive failed calls; after {@code openDurationMs}
 * transitions to half-open and allows one probe. Probe success closes; probe failure re-opens.
 */
public class ConsecutiveFailureCircuitBreaker implements CircuitBreaker {
    private enum State {
        CLOSED, OPEN, HALF_OPEN
    }

    private final int failureThreshold;
    private final long openDurationMs;

    private State state = State.CLOSED;
    private int consecutiveFailures = 0;
    private long openedAtMs = 0;
    private boolean halfOpenProbeAllowed = false;

    public ConsecutiveFailureCircuitBreaker(int failureThreshold, long openDurationMs) {
        if (failureThreshold < 1) {
            throw new IllegalArgumentException("failureThreshold must be >= 1");
        }
        this.failureThreshold = failureThreshold;
        this.openDurationMs = openDurationMs;
    }

    @Override
    public synchronized boolean allowRequest() {
        if (state == State.CLOSED) {
            return true;
        }
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - openedAtMs >= openDurationMs) {
                state = State.HALF_OPEN;
                halfOpenProbeAllowed = true;
            } else {
                return false;
            }
        }
        if (halfOpenProbeAllowed) {
            halfOpenProbeAllowed = false;
            return true;
        }
        return false;
    }

    @Override
    public synchronized void recordSuccess() {
        consecutiveFailures = 0;
        state = State.CLOSED;
        halfOpenProbeAllowed = false;
    }

    @Override
    public synchronized void recordFailure() {
        if (state == State.HALF_OPEN) {
            openCircuit();
            return;
        }
        consecutiveFailures++;
        if (consecutiveFailures >= failureThreshold) {
            openCircuit();
        }
    }

    private void openCircuit() {
        state = State.OPEN;
        openedAtMs = System.currentTimeMillis();
        halfOpenProbeAllowed = false;
    }
}
