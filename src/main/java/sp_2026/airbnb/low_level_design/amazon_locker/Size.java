package sp_2026.airbnb.low_level_design.amazon_locker;

public enum Size {
    SMALL(1),
    MEDIUM(2),
    LARGE(3);

    private int value;

    Size(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
