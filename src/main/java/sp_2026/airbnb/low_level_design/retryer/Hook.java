package sp_2026.airbnb.low_level_design.retryer;

public interface Hook {
    void onAttempt(int attempt);

    void onSuccess(int attempt);

    void onFailure(int attempt, Exception exception);
}
