package sp_2026.airbnb;

public class P0190 {
    public int reverseBits(int n) {
        int res = 0;
        int[] bit = new int[32];
        for (int i=0;i<32;i++){
            bit[i] = n % 2;
            n /= 2;
        }

        int factor = 1;
        for (int i=31;i>=0;i--){
            res += factor * bit[i];
            factor *= 2;
        }

        return res ;
    }
}
