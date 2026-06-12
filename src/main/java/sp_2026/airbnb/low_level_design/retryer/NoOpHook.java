package sp_2026.airbnb.low_level_design.retryer;

public class NoOpHook implements Hook {
    @Override
    public void onAttempt(int attempt) {}

    @Override
    public void onSuccess(int attempt) {}

    @Override
    public void onFailure(int attempt, Exception exception) {}
}
