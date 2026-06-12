package sp_2026.airbnb.coding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Menu — Minimum Cost to Cover Wanted Items
 * Given a menu where each item is a bundle of foods with a price, plus a target set of foods you want,
 * return the minimum total cost to acquire every wanted food (and the chosen bundles). Bundles may overlap;
 * you can buy a bundle more than once.
 *
 * Requirements
 * Input:
 * menu: list of (items: List[str], price: int) — e.g. [(["burger"], 50), (["fries"], 30), (["burger", "wings"], 70)].
 * wanted: list of strings — e.g. ["wings", "fries"].
 * Output: minimum total price plus the list of bundles to purchase.
 * The interviewer typically caps |wanted| at a small number (≤ 3, sometimes ≤ 15) during clarification — this hints at the bitmask state space.
 * Example:
 *
 * menu = [(["a", "b", "c"], 5), (["d", "e"], 2), (["a", "c"], 1)]
 * wanted = ["a", "c", "d"]
 * => 3   # buy ["a","c"] (1) + ["d","e"] (2)
 * Notes
 * The canonical solution is bitmask DP over subsets of wanted. Encode each bundle as the bitmask of which wanted items it covers; reduce to weighted set cover with O(2^k · |menu|).
 * Standard DP recurrence: dp[mask] = min(dp[mask & ~bundle_mask] + price) over all bundles.
 * Watch the clarification: "can a bundle be bought multiple times?" If yes, the recurrence above already handles it; if no, swap to a 0/1 set-cover variant.
 * Items in a bundle that are not in wanted are ignored — they neither help nor hurt; do not over-engineer.
 * For |wanted| ≤ 20, bitmask DP runs in milliseconds; for larger inputs the problem becomes NP-hard and the interviewer expects a greedy approximation discussion.
 * Preparation
 * Implement the bitmask DP cold; trace by hand on the example above to confirm the recurrence picks ["a","c"] + ["d","e"].
 * Be able to reconstruct the chosen bundles (store a parent[mask] pointer alongside dp[mask]).
 * Practice the variant where bundles cannot be repeated — keep both versions in your head.
 * Pair this with split-stay and refund-waterfall since the same loop slot often draws from the bitmask / DP family.
 *
 * Implementation note: {@link #minCost} uses count-state memoized DP (supports duplicate wanted entries,
 * duplicate items inside a bundle, and repeated bundle purchases). The no-duplicate case is a subset.
 */
public class Menu {

    public static final class Bundle {
        public final List<String> items;
        public final int price;

        public Bundle(List<String> items, int price) {
            this.items = items;
            this.price = price;
        }
    }

    public static final class Result {
        public final int minCost;
        public final List<Integer> bundleIndices;

        public Result(int minCost, List<Integer> bundleIndices) {
            this.minCost = minCost;
            this.bundleIndices = bundleIndices;
        }

        public boolean isFeasible() {
            return minCost >= 0;
        }
    }

    /**
     * Returns the minimum total price and menu indices of bundles to buy (in order).
     * If {@code wanted} cannot be fully covered, {@link Result#minCost} is {@code -1}.
     */
    public Result minCost(List<Bundle> menu, List<String> wanted) {
        if (wanted == null || wanted.isEmpty()) {
            return new Result(0, List.of());
        }
        if (menu == null || menu.isEmpty()) {
            return new Result(-1, List.of());
        }

        int[] need = buildNeed(wanted);
        int[][] provide = buildProvide(menu, wanted);
        int[] prices = new int[menu.size()];
        for (int i = 0; i < menu.size(); i++) {
            prices[i] = menu.get(i).price;
        }

        Map<String, Integer> dp = new HashMap<>();
        Map<String, Choice> parent = new HashMap<>();
        int cost = dfs(need, provide, prices, dp, parent);
        if (cost == Integer.MAX_VALUE) {
            return new Result(-1, List.of());
        }
        return new Result(cost, reconstruct(parent, need));
    }

    /** rem 全 0 → 0；否则 memo + 试每个 bundle。 */
    private int dfs(
            int[] rem,
            int[][] provide,
            int[] prices,
            Map<String, Integer> dp,
            Map<String, Choice> parent) {
        if (isZero(rem)) {
            return 0;
        }

        String key = Arrays.toString(rem);
        Integer cached = dp.get(key);
        if (cached != null) {
            return cached;
        }

        int best = Integer.MAX_VALUE;
        Choice pick = null;
        for (int i = 0; i < provide.length; i++) {
            int[] next = afterBuy(rem, provide[i]);
            if (Arrays.equals(rem, next)) {
                continue;
            }
            int cand = prices[i] + dfs(next, provide, prices, dp, parent);
            if (cand < best) {
                best = cand;
                pick = new Choice(next, i);
            }
        }

        dp.put(key, best);
        if (pick != null) {
            parent.put(key, pick);
        }
        return best;
    }

    private static List<Integer> reconstruct(Map<String, Choice> parent, int[] start) {
        List<Integer> chosen = new ArrayList<>();
        String key = Arrays.toString(start);
        while (parent.containsKey(key)) {
            Choice c = parent.get(key);
            chosen.add(c.bundleIdx);
            key = Arrays.toString(c.nextRem);
        }
        return chosen;
    }

    private static int[] afterBuy(int[] rem, int[] provide) {
        int[] next = rem.clone();
        for (int i = 0; i < rem.length; i++) {
            next[i] = Math.max(0, rem[i] - provide[i]);
        }
        return next;
    }

    private static boolean isZero(int[] rem) {
        for (int x : rem) {
            if (x > 0) {
                return false;
            }
        }
        return true;
    }

    /** wanted 每种菜需要几份，顺序稳定。 */
    private static int[] buildNeed(List<String> wanted) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String item : wanted) {
            counts.merge(item, 1, Integer::sum);
        }
        return counts.values().stream().mapToInt(Integer::intValue).toArray();
    }

    /** bundle 对 wanted 每种菜提供几份（忽略不在 wanted 里的菜）。 */
    private static int[][] buildProvide(List<Bundle> menu, List<String> wanted) {
        Map<String, Integer> idx = new LinkedHashMap<>();
        for (String item : wanted) {
            idx.putIfAbsent(item, idx.size());
        }
        int n = idx.size();
        int[][] provide = new int[menu.size()][n];
        for (int b = 0; b < menu.size(); b++) {
            for (String item : menu.get(b).items) {
                Integer j = idx.get(item);
                if (j != null) {
                    provide[b][j]++;
                }
            }
        }
        return provide;
    }

    private static final class Choice {
        final int[] nextRem;
        final int bundleIdx;

        Choice(int[] nextRem, int bundleIdx) {
            this.nextRem = nextRem;
            this.bundleIdx = bundleIdx;
        }
    }
}
