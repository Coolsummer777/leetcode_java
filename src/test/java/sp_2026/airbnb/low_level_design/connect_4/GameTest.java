package sp_2026.airbnb.low_level_design.connect_4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GameTest {

    @Test
    void horizontalWin() {
        Game game = new Game(6, 7, 4);
        // X in columns 0,1,2,3; O plays elsewhere
        game.makeMove(Player.X, 0);
        game.makeMove(Player.O, 6);
        game.makeMove(Player.X, 1);
        game.makeMove(Player.O, 6);
        game.makeMove(Player.X, 2);
        game.makeMove(Player.O, 6);
        game.makeMove(Player.X, 3);

        assertEquals(GameState.PLAYER_X_WINS, game.state());
    }

    @Test
    void verticalWin() {
        Game game = new Game(6, 7, 4);
        // X stacks in column 0
        game.makeMove(Player.X, 0);
        game.makeMove(Player.O, 1);
        game.makeMove(Player.X, 0);
        game.makeMove(Player.O, 1);
        game.makeMove(Player.X, 0);
        game.makeMove(Player.O, 1);
        game.makeMove(Player.X, 0);

        assertEquals(GameState.PLAYER_X_WINS, game.state());
    }

    @Test
    void diagonalWin() {
        Game game = new Game(4, 4, 4);
        // Build diagonal \ for X: (3,0), (2,1), (1,2), (0,3)
        game.makeMove(Player.X, 0);
        game.makeMove(Player.O, 1);
        game.makeMove(Player.X, 1);
        game.makeMove(Player.O, 2);
        game.makeMove(Player.X, 2);
        game.makeMove(Player.O, 3);
        game.makeMove(Player.X, 2);
        game.makeMove(Player.O, 3);
        game.makeMove(Player.X, 3);
        game.makeMove(Player.O, 1);
        game.makeMove(Player.X, 3);

        assertEquals(GameState.PLAYER_X_WINS, game.state());
    }

    @Test
    void fullColumnRejection() {
        Game game = new Game(2, 3, 4);
        game.makeMove(Player.X, 0);
        game.makeMove(Player.O, 0);
        assertThrows(IllegalStateException.class, () -> game.makeMove(Player.X, 0));
    }

    @Test
    void draw() {
        Game game = new Game(2, 2, 4); // K=4 impossible on 2x2, so only draw possible
        game.makeMove(Player.X, 0);
        game.makeMove(Player.O, 1);
        game.makeMove(Player.X, 0);
        game.makeMove(Player.O, 1);

        assertEquals(GameState.DRAW, game.state());
    }

    @Test
    void undoRestoresInProgressGame() {
        Game game = new Game(6, 7, 4);
        game.makeMove(Player.X, 0);
        game.makeMove(Player.O, 1);
        game.undo();

        assertEquals(GameState.IN_PROGRESS, game.state());
        assertEquals(Player.O, game.currentPlayer());
    }

    @Test
    void undoAfterWinAllowsPlayToContinue() {
        Game game = new Game(6, 7, 4);
        game.makeMove(Player.X, 0);
        game.makeMove(Player.O, 6);
        game.makeMove(Player.X, 1);
        game.makeMove(Player.O, 6);
        game.makeMove(Player.X, 2);
        game.makeMove(Player.O, 6);
        game.makeMove(Player.X, 3);
        assertEquals(GameState.PLAYER_X_WINS, game.state());

        game.undo();
        assertEquals(GameState.IN_PROGRESS, game.state());
        assertEquals(Player.X, game.currentPlayer());
        game.makeMove(Player.X, 3);
        assertEquals(GameState.PLAYER_X_WINS, game.state());
    }

    @Test
    void undoAfterDrawAllowsPlayToContinue() {
        Game game = new Game(2, 2, 4);
        game.makeMove(Player.X, 0);
        game.makeMove(Player.O, 1);
        game.makeMove(Player.X, 0);
        game.makeMove(Player.O, 1);
        assertEquals(GameState.DRAW, game.state());

        game.undo();
        assertEquals(GameState.IN_PROGRESS, game.state());
        assertEquals(Player.O, game.currentPlayer());
    }

    @Test
    void undoWithNoMovesThrows() {
        Game game = new Game(6, 7, 4);
        assertThrows(IllegalStateException.class, game::undo);
    }
}
