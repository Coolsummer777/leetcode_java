package sp_2026.airbnb.coding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 
 
Split Stay — Two-Listing Date Coverage
Given a set of Airbnb listings, each with a list of available days, and a requested date range, return every pair of two distinct listings whose combined availability covers the range with no overlap (the first listing covers a contiguous prefix, the second covers the remainder).

Requirements
Implement an API endpoint that takes (listings: Map<String, List<Int>>, startDate: Int, endDate: Int) and returns all unordered pairs of listing names that can form a valid split stay.
A split is valid when listing A covers some contiguous prefix [start, k] and listing B covers the suffix [k+1, end] — both halves must be fully contained in the respective listing's availability set.
Each pair appears once. If a single listing already covers the full range, that's a degenerate result — clarify with the interviewer whether to include single-listing solutions.
Return order is unspecified; many candidates choose lexicographic to make tests stable.
Example:

listings = {
  "A": [1,2,3,6,7,10,11],
  "B": [3,4,5,6,8,9,10,13],
  "C": [7,8,9,10,11]
}
dateRange = [3, 11]
=> [("B", "C")]

Follow-ups:

Bonus credit for a bitmask solution where each listing's availability over the requested window becomes an N-bit mask; the answer is every pair whose OR equals the full-window mask and whose split point exists.
Asked to extend to 3-listing splits or to maximize the number of distinct listings used.
Asked for the API contract (request / response schema, pagination if the listing set is large).


Examples
A - [1,2,3,6,7,10,11]
B - [3,4,5,6,8,9,10,13]
C - [7,8,9,10,11]
range = [3, 11]
expected = [(B, C)]   # B covers 3-6, C covers 7-11
# Brute force reference (cited in one community thread, lightly cleaned)
from itertools import combinations

def find_splits(listings, start, end):
    days = set(range(start, end + 1))
    out = []
    for a, b in combinations(listings.keys(), 2):
        sa, sb = set(listings[a]), set(listings[b])
        # exclude pairs where one listing already covers the range alone
        if days.issubset(sa) or days.issubset(sb):
            continue
        if days.issubset(sa | sb):
            # confirm a contiguous split point exists
            for k in range(start, end):
                if set(range(start, k + 1)).issubset(sa) and set(range(k + 1, end + 1)).issubset(sb):
                    out.append((a, b)); break
                if set(range(start, k + 1)).issubset(sb) and set(range(k + 1, end + 1)).issubset(sa):
                    out.append((a, b)); break
    return out
Notes
The brute-force O(L^2 · D) solution (pairs × days) is acceptable as a first pass; interviewers expect the candidate to either prove the split-point check or recognize the bitmask reduction.
Bitmask reduction: represent each listing as an (end - start + 1)-bit integer; a valid split pair (A, B) satisfies A | B == full_mask and A is a contiguous prefix of full_mask while B is the complementary suffix. This is O(L^2) ignoring the bit width.
Watch the "left can only forward to right" constraint: do not return (B, A) if (A, B) already covers it — the order of names matters when the question phrases it as "first stay / second stay".
The community version of this prompt has minor variants (sometimes the result is the set of unordered pairs; sometimes the candidate is asked to also return the split day). Clarify the exact return shape before coding.
One of the most heavily reused phone-screen + onsite-coding prompts in the bank; expect the interviewer to push past the first working solution into a complexity discussion and at least one follow-up.
Preparation
Write the brute-force version cold in under 10 minutes, then add the split-point validation loop.
Implement the bitmask version on paper, then in code; be able to argue O(L^2 · W/64) runtime where W is the window width.
Practice articulating the API surface (request body, response schema, error cases) — interviewers in 2026 have been pushing on this even in the 45-minute screen.
Write three test cases by hand: (1) no valid pair, (2) one pair, (3) ambiguous case where two different splits cover the same pair — confirm de-duplication.
Pair this drill with the Maximum Profit / Job Scheduling and Menu problems since the same loop slot often draws from this family.

 */



public class SplitStay {

    public List<List<String>> splitStay(Map<String, List<Integer>> list, int startDate, int endDate) {
        List<List<String>> result = new ArrayList<>();

        List<String> keyList = new ArrayList<>(list.keySet());
        keyList.sort((a, b) -> a.compareTo(b));
        int n = keyList.size();
        int[] preIndex = new int[n];
        int[] postIndex = new int[n];

        for (int i = 0; i < n; i++) {
            List<Integer> value = list.get(keyList.get(i));
            boolean[] flag = new boolean[endDate - startDate + 1];
            for (int j = 0; j < value.size(); j++) {
                if (value.get(j) >= startDate && value.get(j) <= endDate) {
                    flag[value.get(j) - startDate] = true;
                }
            }

            preIndex[i] = endDate + 1;
            for (int j = 0; j < flag.length; j++) {
                if (!flag[j]) {
                    preIndex[i] = j + startDate;
                    break;
                }
            }

            postIndex[i] = startDate - 1;
            for (int j = flag.length - 1; j >= 0; j--) {
                if (!flag[j]) {
                    postIndex[i] = j + startDate;
                    break;
                }
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                String key1 = keyList.get(i);
                String key2 = keyList.get(j);
                if (preIndex[i] > postIndex[j] && preIndex[i] > startDate && postIndex[j] < endDate) {
                    result.add(Arrays.asList(key1, key2));
                }
            }
        }

        return result;
    }
}
