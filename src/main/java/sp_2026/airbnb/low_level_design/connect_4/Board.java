package sp_2026.airbnb.low_level_design.connect_4;

public class Board {

    private final int rows;
    private final int cols;
    private final int winLength;
    private final Player[][] grid;
    private int emptyCells;

    public Board(int rows, int cols, int winLength) {
        if (rows <= 0 || cols <= 0 || winLength <= 0) {
            throw new IllegalArgumentException("rows, cols, and winLength must be positive");
        }
        this.rows = rows;
        this.cols = cols;
        this.winLength = winLength;
        this.grid = new Player[rows][cols];
        this.emptyCells = rows * cols;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public int winLength() {
        return winLength;
    }

    public boolean isFull() {
        return emptyCells == 0;
    }

  /**
   * Drops a disc into the lowest empty cell of the column.
   * Returns the row index where the disc landed.
   */
    public int drop(int column, Player player) {
        if (column < 0 || column >= cols) {
            throw new IllegalArgumentException("Invalid column: " + column);
        }
        for (int row = rows - 1; row >= 0; row--) {
            if (grid[row][column] == null) {
                grid[row][column] = player;
                emptyCells--;
                return row;
            }
        }
        throw new IllegalStateException("Column " + column + " is full");
    }

    public void clear(int row, int column) {
        if (row < 0 || row >= rows || column < 0 || column >= cols) {
            throw new IllegalArgumentException("Invalid position: (" + row + ", " + column + ")");
        }
        if (grid[row][column] == null) {
            throw new IllegalStateException("No disc at (" + row + ", " + column + ")");
        }
        grid[row][column] = null;
        emptyCells++;
    }

  /**
   * Returns the longest run of the given player's discs through (row, col)
   * along any of the four axes (horizontal, vertical, both diagonals).
   */
    public int winningRunThrough(int row, int col, Player player) {
        int maxRun = 0;
        maxRun = Math.max(maxRun, countAxis(row, col, player, 0, 1));   // horizontal
        maxRun = Math.max(maxRun, countAxis(row, col, player, 1, 0));   // vertical
        maxRun = Math.max(maxRun, countAxis(row, col, player, 1, 1));   // diagonal \
        maxRun = Math.max(maxRun, countAxis(row, col, player, 1, -1)); // diagonal /
        return maxRun;
    }

    private int countAxis(int row, int col, Player player, int dr, int dc) {
        int count = 1;
        count += countDirection(row, col, player, dr, dc);
        count += countDirection(row, col, player, -dr, -dc);
        return count;
    }

    private int countDirection(int row, int col, Player player, int dr, int dc) {
        int count = 0;
        int r = row + dr;
        int c = col + dc;
        while (r >= 0 && r < rows && c >= 0 && c < cols && grid[r][c] == player) {
            count++;
            r += dr;
            c += dc;
        }
        return count;
    }
}
