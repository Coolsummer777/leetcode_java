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

    public static void handle(){
        String n = "00178";
        String lowerBound = "9";
        System.out.println(n + ":" + lowerBound +"->"+smallestPermuation(n, lowerBound));
    }

    public static String smallestPermuation(String n, String lowerBound){
        StringBuilder sb = new StringBuilder();
        if (lowerBound.length() > n.length()) return "-1";
        if (lowerBound.length() < n.length()){
            for(int i=0;i<n.length() - lowerBound.length();i++){
                sb.append('0');
            }
            sb.append(lowerBound);
            lowerBound = sb.toString();
            sb.setLength(0);
        }
        int[] count = new int[10];
        int[] count2 = new int[10];
        for (int i=0;i<n.length();i++){
            count[n.charAt(i) - '0']++;
            count2[n.charAt(i) - '0']++;
        }

        String largest = getLargest(count);
        if (largest.compareTo(lowerBound) < 0) return "-1";
        if (largest.compareTo(lowerBound) == 0) return largest;

        
        for (int i=0;i<lowerBound.length();i++){
            int c = lowerBound.charAt(i) - '0';
            if (count[c] > 0){
                count[c]--;
                String subLargest = getLargest(count);
                if (subLargest.compareTo(lowerBound.substring(i+1)) < 0){
                    count[c]++;
                }else{
                    sb.append((char)(c + '0'));
                    continue;
                }
            }

            int next = c + 1;
            while (next < 10 && count[next] == 0){
                next++;
            }
            if (next == 10){
                return "-1";
            }
            count[next]--;
            sb.append((char)(next + '0'));
            sb.append(getSmallest(count));
            break;
        }

        if (sb.charAt(0) == '0') {
            sb.setLength(0);
            for (int i=1;i<10;i++){
                if (count2[i] > 0){
                    count2[i]--;
                    sb.append((char)(i + '0'));
                    sb.append(getSmallest(count2));
                    break;
                }
            }
        }

        return sb.toString();
    }

    public static String getSmallest(int[] count){
        StringBuilder sb = new StringBuilder();

        for (int i=0;i<10;i++){
            for (int j=0;j<count[i];j++){
                sb.append((char)(i + '0'));
            }
        }

        return sb.toString();
    }

    public static String getLargest(int[] count){
        StringBuilder sb = new StringBuilder();

        for (int i=9;i>=0;i--){
            for (int j=0;j<count[i];j++){
                sb.append((char)(i + '0'));
            }
        }

        return sb.toString();
    }
}
