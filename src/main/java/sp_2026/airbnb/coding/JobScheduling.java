package sp_2026.airbnb.coding;


import java.util.ArrayList;
import java.util.List;

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

    public int maxProfitUsingTimeBucket(List<Job> jobs) {

        int maxEnd = 0;
        for (Job job : jobs) {
            maxEnd = Math.max(maxEnd, job.end);
        }

        List<List<Job>> timeBucket = new ArrayList<>();
        for (int i = 0; i <= maxEnd; i++) {
            timeBucket.add(new ArrayList<>());
        }

        for (Job job : jobs) {
            timeBucket.get(job.end).add(job);
        }

        int[] dp = new int[maxEnd + 1];
        for (int i = 1; i <= maxEnd; i++) {
            dp[i] = dp[i - 1];
            for (Job job : timeBucket.get(i)) {
                dp[i] = Math.max(dp[i], dp[job.start] + job.profit);
            }
        }

        return dp[maxEnd];
    }


    public int maxProfit(List<Job> jobs) {
        jobs.sort((a, b) -> a.end - b.end);
        int[] dp = new int[jobs.size() + 1];
        dp[0] = 0;

        for (int i = 1; i <= jobs.size(); i++) {
            int j = binarySearch(jobs, jobs.get(i - 1).start, 0, i - 2);
            dp[i] = Math.max(dp[i - 1], dp[j+1] + jobs.get(i - 1).profit);
        }

        return dp[jobs.size()];
    }

    public int binarySearch(List<Job> jobs, int target,int left,int right) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (jobs.get(mid).end <= target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return left - 1;
    }


    class Job {
        int start;
        int end;
        int profit;
    }

}
