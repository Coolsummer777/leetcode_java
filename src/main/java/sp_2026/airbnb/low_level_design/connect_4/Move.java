package sp_2026.airbnb.low_level_design.connect_4;

public class Move {

    private final int row;
    private final int column;
    private final Player player;

    public Move(int row, int column, Player player) {
        this.row = row;
        this.column = column;
        this.player = player;
    }

    public int row() {
        return row;
    }

    public int column() {
        return column;
    }

    public Player player() {
        return player;
    }
}
