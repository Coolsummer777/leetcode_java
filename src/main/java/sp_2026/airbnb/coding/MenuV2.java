package sp_2026.airbnb.coding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
public class MenuV2 {


    public ResultType minCostReveresDP(List<Bundle> menu,List<String> wanted) {
        ResultType res = new ResultType(Integer.MAX_VALUE, new LinkedList<>());

        Map<String,Integer> wantMap = buildWantMap(wanted);
        List<Bundle> userfulBundles = bundleFilter(menu, wantMap);

        int mask = 1 << wanted.size();
        mask -= 1;
        int[] bundleMasks = buildBundleMasks(userfulBundles, wantMap);
        int[] dp = new int[mask + 1];
        int[] parent = new int[mask + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i=1;i<=mask;i++) {

            for (int j=0;j<userfulBundles.size();j++) {
                if ((i & bundleMasks[j]) == 0) {
                    continue;
                }
                int pre = i & ~bundleMasks[j];
                if (dp[pre] == Integer.MAX_VALUE) {
                    continue;
                }
                if (dp[pre] + userfulBundles.get(j).price < dp[i]){
                    dp[i] = dp[pre] + userfulBundles.get(j).price;
                    parent[i] = j;
                }
            }
        }

        if (dp[mask] == Integer.MAX_VALUE) {
            return res;
        }

        res.cost = dp[mask];
        int curr = mask;
        while (curr > 0) {
            int bIdx = parent[curr];
            res.bundles.addFirst(userfulBundles.get(bIdx));
            curr = curr & ~bundleMasks[bIdx];
        }
        


        return res;
    }

    public ResultType minCost(List<Bundle> menu,List<String> wanted) {
        ResultType res = new ResultType(Integer.MAX_VALUE, new LinkedList<>());

        Map<String,Integer> wantMap = buildWantMap(wanted);
        List<Bundle> userfulBundles = bundleFilter(menu, wantMap);

        int mask = 1 << wanted.size();
        mask -= 1;
        int[] bundleMasks = buildBundleMasks(userfulBundles, wantMap);
        int[] dp = new int[mask + 1];
        int[] parent = new int[mask + 1];
        int[] bundleIndex = new int[mask + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i=0;i<=mask;i++) {
            if (dp[i] == Integer.MAX_VALUE) {
                continue;
            }

            for (int j=0;j<userfulBundles.size();j++) {
                int next = i | bundleMasks[j];
                if (next > mask) {
                    continue;
                }
                if (dp[i] + userfulBundles.get(j).price < dp[next]){
                    dp[next] = dp[i] + userfulBundles.get(j).price;
                    parent[next] = i;
                    bundleIndex[next] = j;
                }
            }
        }

        if (dp[mask] == Integer.MAX_VALUE) {
            return res;
        }

        res.cost = dp[mask];
        int curr = mask;
        while (curr > 0) {
            int bIdx = bundleIndex[curr];
            res.bundles.addFirst(userfulBundles.get(bIdx));
            curr = parent[curr];
        }
        


        return res;
    }

    /**
     * 0/1 variant: each menu bundle can be purchased at most once.
     */
    public ResultType minCost01(List<Bundle> menu, List<String> wanted) {
        ResultType res = new ResultType(Integer.MAX_VALUE, new LinkedList<>());

        Map<String, Integer> wantMap = buildWantMap(wanted);
        List<Bundle> userfulBundles = bundleFilter(menu, wantMap);

        int mask = (1 << wanted.size()) - 1;
        int[] bundleMasks = buildBundleMasks(userfulBundles, wantMap);
        int[] dp = new int[mask + 1];
        int[] parent = new int[mask + 1];
        int[] bundleIndex = new int[mask + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int j = 0; j < userfulBundles.size(); j++) {
            int[] prevDp = dp.clone();
            int bm = bundleMasks[j];
            int price = userfulBundles.get(j).price;

            for (int i = 0; i <= mask; i++) {
                if (prevDp[i] == Integer.MAX_VALUE) {
                    continue;
                }
                int next = i | bm;
                if (prevDp[i] + price < dp[next]) {
                    dp[next] = prevDp[i] + price;
                    parent[next] = i;
                    bundleIndex[next] = j;
                }
            }
        }

        if (dp[mask] == Integer.MAX_VALUE) {
            return res;
        }

        res.cost = dp[mask];
        int curr = mask;
        while (curr > 0) {
            int bIdx = bundleIndex[curr];
            res.bundles.addFirst(userfulBundles.get(bIdx));
            curr = parent[curr];
        }

        return res;
    }


    /**
     * 0/1 variant: each menu bundle can be purchased at most once.
     */
    public ResultType minCost01Reverse(List<Bundle> menu, List<String> wanted) {
        ResultType res = new ResultType(Integer.MAX_VALUE, new LinkedList<>());

        Map<String, Integer> wantMap = buildWantMap(wanted);
        List<Bundle> userfulBundles = bundleFilter(menu, wantMap);

        int mask = (1 << wanted.size()) - 1;
        int[] bundleMasks = buildBundleMasks(userfulBundles, wantMap);
        int[] dp = new int[mask + 1];
        int[] parent = new int[mask + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int j = 0; j < userfulBundles.size(); j++) {
            int[] prevDp = dp.clone();
            int bm = bundleMasks[j];
            int price = userfulBundles.get(j).price;

            for (int i = 1; i <= mask; i++) {
                if ((i & bm) == 0) {
                    continue;
                }

                int pre = i & ~bm;
                if (prevDp[pre] == Integer.MAX_VALUE) {
                    continue;
                }
                if (prevDp[pre] + price < dp[i]){
                    dp[i] = prevDp[pre] + price;
                    parent[i] = j;
                }
            }
        }

        if (dp[mask] == Integer.MAX_VALUE) {
            return res;
        }

        res.cost = dp[mask];
        int curr = mask;
        while (curr > 0) {
            int bIdx = parent[curr];
            res.bundles.addFirst(userfulBundles.get(bIdx));
            curr = curr & ~bundleMasks[bIdx];
        }

        return res;
    }



    int[] buildBundleMasks(List<Bundle> menu, Map<String, Integer> wantMap) {
        int[] bundleMasks = new int[menu.size()];
        for (int i = 0; i < menu.size(); i++) {
            Bundle b = menu.get(i);
            int mask = 0;
            for (String item : b.items) {
                if (wantMap.containsKey(item)) {
                    mask |= 1 << wantMap.get(item);
                }
            }
            bundleMasks[i] = mask;
        }
        return bundleMasks;
    }
    Map<String,Integer> buildWantMap(List<String> wanted){
        Map<String, Integer> wantMap = new LinkedHashMap<>();
        for (int i = 0; i < wanted.size(); i++) {
            wantMap.put(wanted.get(i), i);
        }
        return wantMap;
    }

    List<Bundle> bundleFilter(List<Bundle> menu, Map<String, Integer> wantMap) {
        List<Bundle> filtered = new ArrayList<>();
        for (Bundle b : menu) {
            for (String item : b.items) {
                if (wantMap.containsKey(item)) {
                    filtered.add(b);
                    break;
                }
            }
        }
        return filtered;
    }



    class ResultType {
        int cost;
        List<Bundle> bundles;

        public ResultType(int cost, List<Bundle> bundles) {
            this.cost = cost;
            this.bundles = bundles;
        }
    }


    class Bundle{
        List<String> items;
        int price;

        public Bundle(List<String> items, int price) {
            this.items = items;
            this.price = price;
        }
    }

}