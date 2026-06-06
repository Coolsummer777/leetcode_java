package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class SplitStayTest {

    private final SplitStay solver = new SplitStay();

    @Test
    void splitStay_findsPrefixSuffixPairFromSample() {
        Map<String, List<Integer>> listings = new HashMap<>();
        listings.put("A", List.of(1, 2, 3, 6, 7, 10, 11));
        listings.put("B", List.of(3, 4, 5, 6, 8, 9, 10, 13));
        listings.put("C", List.of(7, 8, 9, 10, 11));

        List<List<String>> result = solver.splitStay(listings, 3, 11);

        assertEquals(List.of(List.of("B", "C")), result);
    }

    @Test
    void splitStay_returnsEmptyWhenNoValidSplit() {
        Map<String, List<Integer>> listings = Map.of(
                "A", List.of(1, 2),
                "B", List.of(8, 9));

        List<List<String>> result = solver.splitStay(listings, 3, 7);

        assertEquals(List.of(), result);
    }

    @Test
    void splitStay_findsSplitWithTwoListings() {
        Map<String, List<Integer>> listings = Map.of(
                "A", List.of(1, 2, 3, 4),
                "B", List.of(4, 5, 6, 7));

        List<List<String>> result = solver.splitStay(listings, 3, 6);

        assertEquals(List.of(List.of("A", "B")), result);
    }

    @Test
    void splitStay_findsMultipleValidPairs() {
        Map<String, List<Integer>> listings = new HashMap<>();
        listings.put("Prefix", List.of(1, 2, 3, 4));
        listings.put("Suffix1", List.of(5, 6, 7));
        listings.put("Suffix2", List.of(5, 6, 8));

        List<List<String>> result = solver.splitStay(listings, 3, 6);

        assertEquals(
                List.of(List.of("Prefix", "Suffix1"), List.of("Prefix", "Suffix2")),
                result);
    }

    @Test
    void splitStay_requiresBothListingsToParticipate() {
        Map<String, List<Integer>> listings = Map.of(
                "Full", List.of(3, 4, 5, 6),
                "Tail", List.of(5, 6, 7));

        List<List<String>> result = solver.splitStay(listings, 3, 6);

        assertEquals(List.of(List.of("Full", "Tail")), result);
    }

    @Test
    void splitStay_handlesMinimalTwoPartSplit() {
        Map<String, List<Integer>> listings = Map.of(
                "A", List.of(1, 2, 3),
                "B", List.of(4, 5, 6));

        List<List<String>> result = solver.splitStay(listings, 3, 4);

        assertEquals(List.of(List.of("A", "B")), result);
    }
}
