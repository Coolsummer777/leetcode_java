package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TravelScoreTest {

    private final TravelScore solver = new TravelScore();

    @Test
    void maxScore_collectsIntermediateRewardsOnTheWayToEnd() {
        String[][] travel = {
                {"start", "1", "A"},
                {"start", "5", "A"},
                {"A", "2", "END1"}
        };
        String[][] point = {
                {"A", "10"},
                {"END1", "20"}
        };

        assertEquals(27, solver.maxScore(travel, point));
    }

    @Test
    void maxScore_prefersDetourThroughHighRewardNode() {
        String[][] travel = {
                {"start", "1", "END1"},
                {"start", "1", "A"},
                {"A", "10", "END1"}
        };
        String[][] point = {
                {"A", "100"},
                {"END1", "1"}
        };

        assertEquals(90, solver.maxScore(travel, point));
    }

    @Test
    void maxScore_picksBestAmongMultipleEnds() {
        String[][] travel = {
                {"start", "1", "END1"},
                {"start", "3", "END2"}
        };
        String[][] point = {
                {"END1", "10"},
                {"END2", "5"}
        };

        assertEquals(9, solver.maxScore(travel, point));
    }

    @Test
    void maxScore_matchesExampleFromPrompt() {
        String[][] travel = {
                {"start", "3", "A"},
                {"A", "4", "B"},
                {"B", "5", "END1"}
        };
        String[][] point = {
                {"A", "5"},
                {"B", "6"},
                {"END1", "3"}
        };

        assertEquals(2, solver.maxScore(travel, point));
    }

    @Test
    void maxScore_prefersHigherRewardPathThroughBranchingGraph() {
        String[][] travel = {
                {"start", "2", "A"},
                {"start", "1", "B"},
                {"A", "1", "END1"},
                {"B", "4", "END2"}
        };
        String[][] point = {
                {"A", "4"},
                {"B", "8"},
                {"END1", "10"},
                {"END2", "12"}
        };

        assertEquals(15, solver.maxScore(travel, point));
    }

    @Test
    void maxScore_returnsZeroWhenNoEndIsReachable() {
        String[][] travel = {
                {"start", "1", "A"}
        };
        String[][] point = {
                {"END1", "5"}
        };

        assertEquals(0, solver.maxScore(travel, point));
    }
}
