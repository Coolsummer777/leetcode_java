package sp_2026.airbnb;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class P1041Test {

    @Test
    void isRobotBounded_returnsTrueWhenBackToOrigin() {
        P1041 solver = new P1041();

        boolean result = solver.isRobotBounded("GGLLGG");

        assertTrue(result);
    }

    @Test
    void isRobotBounded_returnsFalseWhenUnbounded() {
        P1041 solver = new P1041();

        boolean result = solver.isRobotBounded("GG");

        assertFalse(result);
    }

    @Test
    void isRobotBounded_returnsTrueWhenDirectionChanges() {
        P1041 solver = new P1041();

        boolean result = solver.isRobotBounded("GL");

        assertTrue(result);
    }

    @Test
    void isRobotBounded_acceptsLowercaseLeftTurn() {
        P1041 solver = new P1041();

        boolean result = solver.isRobotBounded("Gl");

        assertTrue(result);
    }
}
