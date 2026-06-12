package sp_2026.airbnb.low_level_design.connect_4;

public enum Player {
    X, O;

    public Player opponent() {
        return this == X ? O : X;
    }
}
