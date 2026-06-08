package sp_2026.airbnb.coding;

/**
 * 
Layover Experiences — Exact-Fill with Min Count
Given a list of experience durations (rounded to a single decimal) and a layover length `X`, choose the minimum number of bookings whose durations sum to exactly `X`. Experiences can be repeated. If no exact-fill exists, book nothing.

Requirements
Input: experiences: List[float] (each rounded to 1 decimal place, e.g. [3.0, 2.0]), total_hours: float (e.g. 7.0).
Output: the minimum number of experiences whose durations sum to exactly total_hours, or 0 if no combination achieves the exact sum.
Experiences may be repeated arbitrarily.
Example:

experiences = [3.0, 2.0], total_hours = 7.0
-> 3   # 2.0 + 2.0 + 3.0
experiences = [3.6, 2.0], total_hours = 7.5
-> 0   # no exact combination
Notes
This is the classic unbounded-coin-change "min coins for exact target" problem with float inputs.
Scale all inputs by 10 to convert to integers (since durations are 1-decimal); now the standard integer DP applies.
dp[t] = min(dp[t - e] + 1) for e in experiences if t >= e and dp[t-e] != INF. Base: dp[0] = 0.
Time: O(target × |experiences|). With target * 10 ≤ ~10000, this is trivial.
Watch the exact-fill rule: do not return the closest under-fill; return 0 (book nothing) if dp[target] is unreachable.
Edge cases: total_hours == 0 (return 0), duplicates in experiences, very large total_hours (DP is still fine; communicate the scale-up).
Follow-up: minimize bookings under a <= total_hours (no exact fill) constraint instead of == total_hours. The recurrence is the same, but the answer is the smallest dp[t] over t <= target.
Preparation
Implement the integer DP in under 15 minutes; cover with 4 tests: exact fit, no exact fit, single-experience-repeated, target = 0.
Practice articulating the float-to-integer scaling before coding — many candidates over-engineer with float DP and lose accuracy.
Drill the DFS + memo variant as a backup (some interviewers ask to enumerate the chosen experiences, not just the count — memoize on the remaining target).
Pair-prep with menu-min-cost-bitmask since both are DP-on-target problems often picked from the same family.



 */
public class LayoverExperiences {

}
