package sp_2026.airbnb;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class P0001Test {

    @Test
    void twoSum_findsExpectedPair() {
        P0001 solver = new P0001();

        int[] result = solver.twoSum(new int[] {2, 7, 11, 15}, 9);

        assertArrayEquals(new int[] {0, 1}, result);
    }

    @Test
    void twoSum_handlesDuplicateValues() {
        P0001 solver = new P0001();

        int[] result = solver.twoSum(new int[] {3, 3}, 6);

        assertArrayEquals(new int[] {0, 1}, result);
    }

    @Test
    void twoSum_handlesNegativeNumbers() {
        P0001 solver = new P0001();

        int[] result = solver.twoSum(new int[] {-3, 4, 3, 90}, 0);

        assertArrayEquals(new int[] {0, 2}, result);
    }
}
