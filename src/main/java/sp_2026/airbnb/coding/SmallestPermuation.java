package sp_2026.airbnb.coding;

/**
 * 

Smallest Permutation ≥ Lower Bound
Given an integer `n` (e.g. `178`) and a lower bound `L` (e.g. `200`), return the smallest integer ≥ `L` that can be formed by rearranging the digits of `n` (leading zeros allowed in the source but the result must not have leading zero unless `n == 0`).

Requirements
Input: n (integer) and lowerBound (integer, optional — without it, return the smallest permutation overall).
Output: the smallest integer that uses exactly the multiset of digits of n and is >= lowerBound; return -1 if no such permutation exists.
Leading zeros in the input multiset are allowed (e.g. n = 100 has digits {1, 0, 0} and produces 100 as the smallest), but the output should not have a leading zero.
Examples:

n=178, lowerBound=None -> 178            # already smallest
n=178, lowerBound=200  -> 718            # next perm > 200 using {1,7,8}
n=178, lowerBound=900  -> -1             # no permutation reaches 900
n=120, lowerBound=200  -> 201
Notes
Without a lower bound, sort digits ascending; if the smallest is zero, swap the first non-zero to the front to avoid leading zero.
With a lower bound, construct the answer digit-by-digit: at position i, pick the smallest unused digit that is >= L[i]. If you pick a digit > L[i], the remaining positions can be the smallest sorted suffix (the bound is already cleared). If you pick == L[i], recurse on i+1; if you cannot pick any, backtrack and try the next-larger digit at position i-1.
This is the digit-DP / next-permutation hybrid pattern. Time complexity is O(D^2) for D digits with backtracking; for small D (typical inputs are ≤ 18 digits) it is essentially constant.
Edge cases: lowerBound has more digits than n (return -1), lowerBound has fewer digits (return the smallest no-leading-zero permutation of n), all digits are zero, duplicates in the multiset.
The community version is sometimes phrased as "next permutation greater than L"; the algorithm is identical.
Preparation
Implement the digit-by-digit constructor cold; verify on n=178, L=200 returns 718 (not 781).
Write the digit-count comparison guard at the start: if len(str(L)) > D, return -1.
Drill the leading-zero corner case with n=100, L=10.
Prepare for the follow-up "what about the next permutation given the same digits (no lower bound)?" — the canonical next-permutation algorithm in O(D) is the gold-standard answer.

 * 
 */
public class SmallestPermuation {

}
