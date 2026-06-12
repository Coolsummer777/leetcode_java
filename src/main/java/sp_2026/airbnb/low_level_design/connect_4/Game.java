package sp_2026.airbnb.low_level_design.connect_4;

import java.util.ArrayDeque;

/**
 *
Connect-4-Style Board Game
Implement the rules of a two-player drop-disc board game (Connect-4 family but not necessarily the standard variant). Players alternate, discs fall to the lowest empty slot in a column, win condition is `K` in a row in any of 8 directions.

Requirements
make_move(player, column) drops a disc into the lowest empty cell of the column; raise if the column is full.
After each move, check whether the moving player has formed a winning line of K in a row.
Support arbitrary board dimensions and arbitrary K.
Track game state: IN_PROGRESS, PLAYER_X_WINS, PLAYER_O_WINS, DRAW.
Notes
The OOD signal is the main grade — clean separation between Board (grid + falling logic), Game (turn order + win-detection), and Player.
Win detection: after dropping at (r, c), scan the 4 axes (horizontal, vertical, both diagonals) outward from (r, c) for the longest run of the moving player's disc; if any axis has >= K, the player wins. This is O(K) per move, not O(R · C).
Edge cases: column full (raise), invalid column index, draw when the board fills with no winner, K larger than the smaller board dimension (game cannot be won — clarify behavior).
Mild variants in the wild: "Othello-style flip" rules, "gravity off" (disc placed exactly at clicked cell), "N players" instead of 2. Clarify before coding.
Many candidates over-engineer a generic AbstractBoardGame framework; the interviewer wants a tight Connect-4-class implementation with the obvious extension hooks, not a framework.
Preparation
Implement Board.drop(column, color), Board.winning_run_through(r, c, k), and Game.play(column) in under 30 minutes.
Write 5 tests: simple horizontal win, vertical win, diagonal win, full-column rejection, draw.
Drill the win-detection scan — the most common bug is off-by-one when scanning outward in both directions.
Pre-script the "what if we wanted to add AI" answer: minimax with alpha-beta on O(K) evaluation.


 */
public class Game {

    private final Board board;
    private final ArrayDeque<Move> moveHistory;
    private Player currentPlayer;
    private GameState state;

    public Game(int rows, int cols, int winLength) {
        this.board = new Board(rows, cols, winLength);
        this.moveHistory = new ArrayDeque<>();
        this.currentPlayer = Player.X;
        this.state = GameState.IN_PROGRESS;
    }

    public GameState state() {
        return state;
    }

    public Player currentPlayer() {
        return currentPlayer;
    }

    public Board board() {
        return board;
    }

    public void makeMove(Player player, int column) {
        if (state != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Game is over: " + state);
        }
        if (player != currentPlayer) {
            throw new IllegalArgumentException("Not " + player + "'s turn");
        }

        int row = board.drop(column, player);
        moveHistory.push(new Move(row, column, player));

        if (board.winningRunThrough(row, column, player) >= board.winLength()) {
            state = player == Player.X ? GameState.PLAYER_X_WINS : GameState.PLAYER_O_WINS;
            return;
        }

        if (board.isFull()) {
            state = GameState.DRAW;
            return;
        }

        currentPlayer = player.opponent();
    }

    public void undo() {
        if (moveHistory.isEmpty()) {
            throw new IllegalStateException("No moves to undo");
        }

        Move last = moveHistory.pop();
        board.clear(last.row(), last.column());
        currentPlayer = last.player();
        state = GameState.IN_PROGRESS;
    }
}
