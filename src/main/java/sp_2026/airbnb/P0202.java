package sp_2026.airbnb;

import java.util.HashSet;
import java.util.Set;

public class P0202{
    public boolean isHappy(int n) {

        Set<Integer> set = new HashSet<>();
        while (true) {
            if (set.contains(n)) return false;
            set.add(n);
            n = calculate(n);
            if (n == 1) return true;
        }
        
    }

    public int calculate(int n){
        int res = 0;

        while (n > 0) {
            int tmp = n % 10;
            res += tmp * tmp;
            n /= 10;
        }

        return res;
    }
}