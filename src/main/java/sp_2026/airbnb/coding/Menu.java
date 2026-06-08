package sp_2026.airbnb.coding;

/**
 * 
Menu — Minimum Cost to Cover Wanted Items
Given a menu where each item is a bundle of foods with a price, plus a target set of foods you want, return the minimum total cost to acquire every wanted food (and the chosen bundles). Bundles may overlap; you can buy a bundle more than once.

Requirements
Input:
menu: list of (items: List[str], price: int) — e.g. [(["burger"], 50), (["fries"], 30), (["burger", "wings"], 70)].
wanted: list of strings — e.g. ["wings", "fries"].
Output: minimum total price plus the list of bundles to purchase.
The interviewer typically caps |wanted| at a small number (≤ 3, sometimes ≤ 15) during clarification — this hints at the bitmask state space.
Example:

menu = [(["a", "b", "c"], 5), (["d", "e"], 2), (["a", "c"], 1)]
wanted = ["a", "c", "d"]
=> 3   # buy ["a","c"] (1) + ["d","e"] (2)
Notes
The canonical solution is bitmask DP over subsets of wanted. Encode each bundle as the bitmask of which wanted items it covers; reduce to weighted set cover with O(2^k · |menu|).
Standard DP recurrence: dp[mask] = min(dp[mask & ~bundle_mask] + price) over all bundles.
Watch the clarification: "can a bundle be bought multiple times?" If yes, the recurrence above already handles it; if no, swap to a 0/1 set-cover variant.
Items in a bundle that are not in wanted are ignored — they neither help nor hurt; do not over-engineer.
For |wanted| ≤ 20, bitmask DP runs in milliseconds; for larger inputs the problem becomes NP-hard and the interviewer expects a greedy approximation discussion.
Preparation
Implement the bitmask DP cold; trace by hand on the example above to confirm the recurrence picks ["a","c"] + ["d","e"].
Be able to reconstruct the chosen bundles (store a parent[mask] pointer alongside dp[mask]).
Practice the variant where bundles cannot be repeated — keep both versions in your head.
Pair this with split-stay and refund-waterfall since the same loop slot often draws from the bitmask / DP family.



 */
public class Menu {

}
