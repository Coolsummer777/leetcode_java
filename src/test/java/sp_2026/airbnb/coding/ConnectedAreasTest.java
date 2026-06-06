package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ConnectedAreasTest {

    @Test
    void emptyBoard_returnsZero() {
        assertEquals(0, ConnectedAreas.calculateScore(new String[0][]));
    }

    @Test
    void singleRegionCoveringWholeBoard() {
        String[][] grid = {
            {"G1", "G2"},
            {"G3", "G1"}
        };
        // 4 cells × (1+2+3+1) = 28
        assertEquals(28, ConnectedAreas.calculateScore(grid));
    }

    @Test
    void multipleRegions_withMixedCrownCounts() {
        String[][] grid = {
            {"G1", "G2", "W1"},
            {"G3", "G1", "W0"},
            {"S2", "S3", "S1"}
        };
        // G: 4×7=28, W: 2×1=2, S: 3×6=18 → 48
        assertEquals(48, ConnectedAreas.calculateScore(grid));
    }

    @Test
    void parseString_parsesCommaSeparatedRows() {
        String raw = "G1,G2,W0\nG2,G3,W0";
        String[][] expected = {{"G1", "G2", "W0"}, {"G2", "G3", "W0"}};
        assertArrayEquals(expected, ConnectedAreas.parseString(raw));
    }

    @Test
    void parseString_trimsWhitespaceAndSkipsBlankLines() {
        String raw = " G1 , G2 \n\n G3 , G1 ";
        String[][] expected = {{"G1", "G2"}, {"G3", "G1"}};
        assertArrayEquals(expected, ConnectedAreas.parseString(raw));
    }

    @Test
    void parseString_emptyInput_returnsEmptyGrid() {
        assertEquals(0, ConnectedAreas.parseString("").length);
        assertEquals(0, ConnectedAreas.parseString("  \n  ").length);
    }

    @Test
    void parseString_invalidTile_throws() {
        assertThrows(IllegalArgumentException.class, () -> ConnectedAreas.parseString("G1,XX,G2"));
    }

    @Test
    void parseString_raggedGrid_throws() {
        assertThrows(IllegalArgumentException.class, () -> ConnectedAreas.parseString("G1,G2\nG3"));
    }

    @Test
    void parseString_thenCalculateScore_matchesHandBuiltGrid() {
        String raw = "G1,G2\nG3,G1";
        assertEquals(28, ConnectedAreas.calculateScore(ConnectedAreas.parseString(raw)));
    }
}
