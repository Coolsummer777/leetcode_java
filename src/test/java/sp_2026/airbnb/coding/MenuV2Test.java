package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MenuV2} bitmask DP (unique wanted items only).
 * Covers unlimited ({@link MenuV2#minCost}, {@link MenuV2#minCostReveresDP})
 * and 0/1 ({@link MenuV2#minCost01}, {@link MenuV2#minCost01Reverse}) variants.
 */
class MenuV2Test {

    private MenuV2 menuV2;

    @BeforeEach
    void setUp() {
        menuV2 = new MenuV2();
    }

    private MenuV2.Bundle bundle(List<String> items, int price) {
        return menuV2.new Bundle(items, price);
    }

    private int sumPrices(List<MenuV2.Bundle> bundles) {
        int sum = 0;
        for (MenuV2.Bundle b : bundles) {
            sum += b.price;
        }
        return sum;
    }

    private void assertCoversWanted(List<String> wanted, MenuV2.ResultType result) {
        Set<String> covered = new HashSet<>();
        for (MenuV2.Bundle b : result.bundles) {
            covered.addAll(b.items);
        }
        for (String item : wanted) {
            assertTrue(covered.contains(item),
                    () -> "missing wanted item " + item + " in " + result.bundles);
        }
    }

    private void assertFeasible(List<String> wanted, MenuV2.ResultType result, int expectedCost) {
        assertTrue(result.cost < Integer.MAX_VALUE, () -> "expected feasible result");
        assertEquals(expectedCost, result.cost);
        assertEquals(expectedCost, sumPrices(result.bundles));
        assertCoversWanted(wanted, result);
    }

    private void assertInfeasible(MenuV2.ResultType result) {
        assertEquals(Integer.MAX_VALUE, result.cost);
        assertTrue(result.bundles.isEmpty());
    }

    private void assertUnlimitedSolvers(List<MenuV2.Bundle> menu, List<String> wanted, int expectedCost) {
        assertFeasible(wanted, menuV2.minCost(menu, wanted), expectedCost);
        assertFeasible(wanted, menuV2.minCostReveresDP(menu, wanted), expectedCost);
    }

    private void assert01Solvers(List<MenuV2.Bundle> menu, List<String> wanted, int expectedCost) {
        assertFeasible(wanted, menuV2.minCost01(menu, wanted), expectedCost);
        assertFeasible(wanted, menuV2.minCost01Reverse(menu, wanted), expectedCost);
    }

    private void assertAllSolvers(List<MenuV2.Bundle> menu, List<String> wanted, int expectedCost) {
        assertUnlimitedSolvers(menu, wanted, expectedCost);
        assert01Solvers(menu, wanted, expectedCost);
    }

    private void assertAllInfeasible(List<MenuV2.Bundle> menu, List<String> wanted) {
        assertInfeasible(menuV2.minCost(menu, wanted));
        assertInfeasible(menuV2.minCostReveresDP(menu, wanted));
        assertInfeasible(menuV2.minCost01(menu, wanted));
        assertInfeasible(menuV2.minCost01Reverse(menu, wanted));
    }

    @Test
    void problemExample() {
        List<MenuV2.Bundle> menu = List.of(
                bundle(List.of("a", "b", "c"), 5),
                bundle(List.of("d", "e"), 2),
                bundle(List.of("a", "c"), 1));
        assertAllSolvers(menu, List.of("a", "c", "d"), 3);
    }

    @Test
    void singleBundleCoversAll() {
        List<MenuV2.Bundle> menu = List.of(
                bundle(List.of("a", "b"), 5),
                bundle(List.of("a"), 3),
                bundle(List.of("b"), 4));
        assertAllSolvers(menu, List.of("a", "b"), 5);
    }

    @Test
    void cheaperToCombineTwoBundles() {
        List<MenuV2.Bundle> menu = List.of(
                bundle(List.of("a"), 2),
                bundle(List.of("b"), 3),
                bundle(List.of("a", "b"), 10));
        assertAllSolvers(menu, List.of("a", "b"), 5);
    }

    @Test
    void ignoresUnwantedItemsInBundle() {
        List<MenuV2.Bundle> menu = List.of(
                bundle(List.of("a", "z"), 4),
                bundle(List.of("b"), 2));
        assertAllSolvers(menu, List.of("a", "b"), 6);
    }

    @Test
    void skipsBundlesWithNoWantedOverlap() {
        List<MenuV2.Bundle> menu = List.of(
                bundle(List.of("x", "y"), 1),
                bundle(List.of("a"), 5));
        assertAllSolvers(menu, List.of("a"), 5);
    }

    @Test
    void emptyWanted() {
        assertAllSolvers(List.of(bundle(List.of("a"), 10)), List.of(), 0);
    }

    @Test
    void infeasible() {
        assertAllInfeasible(
                List.of(bundle(List.of("a"), 1)),
                List.of("b"));
    }

    @Test
    void infeasible_partialCoverageOnly() {
        List<MenuV2.Bundle> menu = List.of(
                bundle(List.of("a"), 1),
                bundle(List.of("b"), 1));
        assertAllInfeasible(menu, List.of("a", "b", "c"));
    }

    @Test
    void overlappingBundlesPickMinimum() {
        List<MenuV2.Bundle> menu = List.of(
                bundle(List.of("a", "b", "c"), 100),
                bundle(List.of("a", "b"), 4),
                bundle(List.of("c"), 1));
        assertAllSolvers(menu, List.of("a", "b", "c"), 5);
    }

    @Test
    void allSolversAgreeOnLargerCase() {
        List<MenuV2.Bundle> menu = List.of(
                bundle(List.of("a", "b"), 8),
                bundle(List.of("b", "c"), 7),
                bundle(List.of("a", "c"), 6),
                bundle(List.of("a"), 3),
                bundle(List.of("b"), 3),
                bundle(List.of("c"), 3),
                bundle(List.of("d"), 2));
        assertAllSolvers(menu, List.of("a", "b", "c", "d"), 11);
    }

    @Test
    void minCost01ForwardAndReverseAgree() {
        List<MenuV2.Bundle> menu = List.of(
                bundle(List.of("a"), 1),
                bundle(List.of("a"), 1),
                bundle(List.of("b"), 1));
        List<String> wanted = List.of("a", "b");

        MenuV2.ResultType forward01 = menuV2.minCost01(menu, wanted);
        MenuV2.ResultType reverse01 = menuV2.minCost01Reverse(menu, wanted);

        assertFeasible(wanted, forward01, 2);
        assertFeasible(wanted, reverse01, 2);
        assertEquals(forward01.cost, reverse01.cost);
        assertTrue(forward01.bundles.size() <= menu.size());
        assertTrue(reverse01.bundles.size() <= menu.size());
    }

    @Test
    void unlimitedCanMatch01WhenEachBundleUsedOnce() {
        List<MenuV2.Bundle> menu = List.of(
                bundle(List.of("a"), 1),
                bundle(List.of("a"), 1),
                bundle(List.of("b"), 1));
        List<String> wanted = List.of("a", "b");

        assertUnlimitedSolvers(menu, wanted, 2);
        assert01Solvers(menu, wanted, 2);
    }
}
