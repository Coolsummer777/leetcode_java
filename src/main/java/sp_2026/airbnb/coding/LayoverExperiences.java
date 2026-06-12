package sp_2026.airbnb.coding;


import java.util.*;
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
    public int minExperiences(List<Float> experiences, float totalHours) {
        int total = Math.round(totalHours * 10);
        if (total == 0) {
            return 0;
        }
        

        Set<Integer> exp = new HashSet<>();
        for (Float f : experiences) {
            int e = Math.round(f * 10);
            if (e == total) {
                return 1;
            }
            if (e < total && e > 0) {
                exp.add(e);
            }
        }

        int[] dp = new int[total + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int t = 1; t <= total; t++) {
            for (int e : exp) {
                if (t >= e && dp[t - e] != Integer.MAX_VALUE) {
                    dp[t] = Math.min(dp[t], dp[t - e] + 1);
                }
            }
        }

        return dp[total] == Integer.MAX_VALUE ? 0 : dp[total];
    }

    /**
     * Returns indices of a minimum-size booking that sums to {@code totalHours} exactly,
     * or an empty list if no such combination exists.
     */
    public List<Integer> minExperiencesV2(List<Float> experiences, float totalHours) {
        int total = Math.round(totalHours * 10);
        if (total == 0) {
            return List.of();
        }

        int n = experiences.size();
        int[] durations = new int[n];
        Map<Integer, Integer> durToIdx = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int e = Math.round(experiences.get(i) * 10);
            durations[i] = e;
            if (e == total) {
                return List.of(i);
            }
            if (e < total && e > 0) {
                durToIdx.merge(e, i, Math::min);
            }
        }

        if (durToIdx.isEmpty()) {
            return List.of();
        }

        int[] parent = new int[total + 1];
        Arrays.fill(parent, -1);
        parent[0] = 0;

        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(0);

        while (!queue.isEmpty()) {
            int sum = queue.poll();
            for (var entry : durToIdx.entrySet()) {
                int d = entry.getKey();
                int idx = entry.getValue();
                int next = sum + d;
                if (next <= total && parent[next] == -1) {
                    parent[next] = idx;
                    if (next == total) {
                        return reconstructPath(parent, durations, total);
                    }
                    queue.offer(next);
                }
            }
        }

        return List.of();
    }

    private List<Integer> reconstructPath(int[] parent, int[] durations, int total) {
        LinkedList<Integer> indices = new LinkedList<>();
        int cur = total;
        while (cur > 0) {
            int idx = parent[cur];
            indices.addFirst(idx);
            cur -= durations[idx];
        }
        return indices;
    }
}
