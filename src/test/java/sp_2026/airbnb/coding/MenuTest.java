package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuTest {

    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = new Menu();
    }

    private Menu.Bundle bundle(List<String> items, int price) {
        return new Menu.Bundle(items, price);
    }

    private int totalCost(List<Menu.Bundle> menuItems, List<Integer> indices) {
        int sum = 0;
        for (int idx : indices) {
            sum += menuItems.get(idx).price;
        }
        return sum;
    }

    private Map<String, Integer> coveredCounts(List<Menu.Bundle> menuItems, List<Integer> indices) {
        Map<String, Integer> counts = new HashMap<>();
        for (int idx : indices) {
            for (String item : menuItems.get(idx).items) {
                counts.merge(item, 1, Integer::sum);
            }
        }
        return counts;
    }

    private Map<String, Integer> wantedCounts(List<String> wanted) {
        Map<String, Integer> counts = new HashMap<>();
        for (String item : wanted) {
            counts.merge(item, 1, Integer::sum);
        }
        return counts;
    }

    private void assertCoversWanted(List<Menu.Bundle> menuItems, List<String> wanted, Menu.Result result) {
        assertTrue(result.isFeasible());
        assertEquals(result.minCost, totalCost(menuItems, result.bundleIndices));

        Map<String, Integer> have = coveredCounts(menuItems, result.bundleIndices);
        for (Map.Entry<String, Integer> entry : wantedCounts(wanted).entrySet()) {
            assertTrue(have.getOrDefault(entry.getKey(), 0) >= entry.getValue(),
                    () -> "missing " + entry.getKey() + " in " + result.bundleIndices);
        }
    }

    @Test
    void problemExample_noDuplicates() {
        List<Menu.Bundle> menuItems = List.of(
                bundle(List.of("a", "b", "c"), 5),
                bundle(List.of("d", "e"), 2),
                bundle(List.of("a", "c"), 1));
        List<String> wanted = List.of("a", "c", "d");

        Menu.Result result = menu.minCost(menuItems, wanted);

        assertEquals(3, result.minCost);
        assertCoversWanted(menuItems, wanted, result);
        assertEquals(2, result.bundleIndices.size());
        assertTrue(result.bundleIndices.contains(1) && result.bundleIndices.contains(2));
    }

    @Test
    void duplicateWanted_requiresBuyingSameBundleTwice() {
        List<Menu.Bundle> menuItems = List.of(
                bundle(List.of("wings"), 30),
                bundle(List.of("wings", "fries"), 50));
        List<String> wanted = List.of("wings", "wings", "fries");

        Menu.Result result = menu.minCost(menuItems, wanted);

        assertEquals(80, result.minCost);
        assertCoversWanted(menuItems, wanted, result);
        assertEquals(2, result.bundleIndices.size());
        assertTrue(result.bundleIndices.contains(0) && result.bundleIndices.contains(1));
    }

    @Test
    void duplicateItemsInsideBundle() {
        List<Menu.Bundle> menuItems = List.of(
                bundle(List.of("wings", "wings"), 45),
                bundle(List.of("wings"), 30));
        List<String> wanted = List.of("wings", "wings");

        Menu.Result result = menu.minCost(menuItems, wanted);

        assertEquals(45, result.minCost);
        assertCoversWanted(menuItems, wanted, result);
        assertEquals(List.of(0), result.bundleIndices);
    }

    @Test
    void emptyWanted() {
        Menu.Result result = menu.minCost(
                List.of(bundle(List.of("a"), 10)),
                List.of());

        assertEquals(0, result.minCost);
        assertTrue(result.bundleIndices.isEmpty());
    }

    @Test
    void infeasible() {
        Menu.Result result = menu.minCost(
                List.of(bundle(List.of("a"), 1)),
                List.of("b"));

        assertFalse(result.isFeasible());
        assertEquals(-1, result.minCost);
        assertTrue(result.bundleIndices.isEmpty());
    }

    @Test
    void ignoresUnwantedItemsInBundle() {
        List<Menu.Bundle> menuItems = List.of(
                bundle(List.of("a", "z"), 4),
                bundle(List.of("b"), 2));
        List<String> wanted = List.of("a", "b");

        Menu.Result result = menu.minCost(menuItems, wanted);

        assertEquals(6, result.minCost);
        assertCoversWanted(menuItems, wanted, result);
    }

    @Test
    void repeatedBundlePurchaseIsOptimal() {
        List<Menu.Bundle> menuItems = List.of(
                bundle(List.of("a"), 2),
                bundle(List.of("a", "b"), 100));
        List<String> wanted = List.of("a", "a");

        Menu.Result result = menu.minCost(menuItems, wanted);

        assertEquals(4, result.minCost);
        assertCoversWanted(menuItems, wanted, result);
        assertEquals(List.of(0, 0), result.bundleIndices);
    }
}
