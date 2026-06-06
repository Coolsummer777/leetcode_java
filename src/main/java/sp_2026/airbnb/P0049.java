package sp_2026.airbnb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class P0049 {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String,List<String>> map = new HashMap<>();

        for (String s:strs){
            String key = sort(s);
            if (map.containsKey(key)){
                map.get(key).add(s);
            }else{
                List<String> list = new ArrayList<>();
                list.add(s);
                map.put(key, list);
            }
        }


        List<List<String>> res = new ArrayList<>();
        for (List<String> list:map.values()){
            res.add(list);
        }
        return res;
        
    }

    public String sort(String s){
        int[] count = new int[26];

        for (int i=0;i<s.length();i++){
            count[i-'a']++;
        }

        StringBuilder sb = new StringBuilder();

        for (int i=0;i<26;i++){
            if (count[i] > 0){
                sb.append(count[i]);
                sb.append((char)(i + 'a'));
            }
        }

        return sb.toString();
    }
}