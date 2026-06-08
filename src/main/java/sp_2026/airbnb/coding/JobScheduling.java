package sp_2026.airbnb.coding;


/**
 * 

Maximum Profit in Job Scheduling
Given a set of jobs each with `(start, end, profit)`, choose a subset of non-overlapping jobs that maximizes total profit. Asked at Airbnb both as a phone-screen warmup and as an onsite-coding prompt; the senior-staff follow-up pushes past the standard `O(n log n)` DP toward an even tighter bound.


Requirements
Input: list of jobs (start, end, profit).
Output: the maximum total profit over any subset of non-overlapping jobs (jobs i, j are compatible if end_i <= start_j or end_j <= start_i).
Notes
The canonical solution: sort jobs by end_time; let dp[i] be the max profit using jobs 0..i. Recurrence: dp[i] = max(dp[i-1], profit[i] + dp[p(i)]) where p(i) is the largest index < i with end[p(i)] <= start[i], found by binary search on the sorted end-times.
Time: O(n log n) for sort + binary searches.
A greedy by end_time alone (earliest-deadline) is wrong when profits vary; the DP is mandatory.
Edge cases: empty input, all jobs identical, intervals touching at endpoints (clarify <= vs <).
Follow-up that has surfaced in the senior-staff round: "the basic solution is O(n log n); can you do better under a stronger assumption?" The standard answer is "no in the general case, but if all start, end are bounded small integers, switch to a O(n + T) bucket / scan over the timeline." Several candidates were specifically pushed on this.
Often paired with the split-stay or refund prompt in the same loop.
Preparation
Implement the sort + binary-search DP cold in under 20 minutes.
Pre-write the bucket / scan variant for the bounded-time follow-up.
Hand-trace [(1,3,50), (2,5,20), (3,10,100), (6,19,200)] to confirm dp = [50, 50, 150, 250].
Drill articulating "why greedy fails on profit-weighted intervals" in one sentence.
 */


public class JobScheduling {

}
