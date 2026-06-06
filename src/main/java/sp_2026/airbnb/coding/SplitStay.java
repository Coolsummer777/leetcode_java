package sp_2026.airbnb.coding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
