package sp_2026.airbnb;

public class P0136 {
    public int singleNumber(int[] nums) {
        int res = 0;

        for (int num : nums){
            res = res ^ num;
        }

        return res;
    }
}
