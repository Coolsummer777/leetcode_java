package sp_2026.airbnb.coding;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeyValue implements KV {

    private static final int CALCULATE_AT_SAVE = 1;

    private Map<String,Node> map;

    private final int mode;

    public KeyValue(int mode){
        map = new HashMap<>();
        this.mode = mode;
    }

    @Override
    public void setValue(String key,int value){

        int originalValue = 0;
        Node node = map.getOrDefault(key, new Node());
        originalValue = node.value;
        node.value = value;

        if (mode == CALCULATE_AT_SAVE){

            // 删除下游对自己的引用
            for (String refKey:node.downStream.keySet()){
                updateUpStream(key,refKey, node.downStream.get(refKey));
            }
            
            // 更新自己上游的值
            for (String refKey:node.upStream.keySet()){
                updateValue(refKey, node.upStream.get(refKey) * (value - originalValue));
            }
        }

        node.downStream.clear();


        map.put(key, node);
    }

    @Override
    public boolean setRef(String key,List<String> ref){


        if (!checkRef(key, ref)){
            return false;
        }

        Node node = map.getOrDefault(key, new Node());


        if (mode == CALCULATE_AT_SAVE){
            int originalValue = node.value;
            // 删除下游对自己的引用
            for (String refKey:node.downStream.keySet()){
                updateUpStream(key, refKey, node.downStream.get(refKey));
            }

            // 构造自己的下游
            node.downStream.clear();
            for (String refKey:ref){
                node.downStream.put(refKey, node.downStream.getOrDefault(refKey, 0) + 1);
            }

            // 从下游更新自己的值
            int sum = 0;
            for (String refKey:node.downStream.keySet()){
                sum += getValue(refKey) * node.downStream.get(refKey);
            }
            node.value = sum;

            // 更新自己上游的值
            for (String refKey:node.upStream.keySet()){
                updateValue(refKey, node.upStream.get(refKey) * (node.value - originalValue));
            }

            // 把自己添加到下游的引用里
            for (String refKey:node.downStream.keySet()){
                updateUpStream(key, refKey, node.downStream.get(refKey) * -1);
            }
        }else{
            node.downStream.clear();
            for (String refKey:ref){
                node.downStream.put(refKey, node.downStream.getOrDefault(refKey, 0) + 1);
            }
        }

        map.put(key, node);

        return true;
    }

    @Override
    public Integer getValue(String key){


        if (!map.containsKey(key)){
            return null;
        }

        Node node = map.get(key);

        if (mode == CALCULATE_AT_SAVE){
            return node.value;
        }

        if (node.downStream.isEmpty()){
            return node.value;
        }

        int sum = 0;
        for (String refKey:node.downStream.keySet()){
            sum += getValue(refKey) * node.downStream.get(refKey);
        }

        return sum;
    }

    // ------------------------------------------------------------------------------------------------
    private void updateUpStream(String refKey,String key,int refCount){
        
        Node node = map.getOrDefault(key, new Node());
        int count = node.upStream.getOrDefault(refKey, 0) - refCount;
        if (count <= 0){
            node.upStream.remove(refKey);
        }else{
            node.upStream.put(refKey, count);
        }
        map.put(key, node);
    }

    private boolean checkRef(String key,List<String> ref){

        if (ref.size() == 0){
            return true;
        }

        Set<String> set = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        set.add(key);
        
        for (String refKey:ref){
            if (refKey.equals(key)){
                return false;
            }

            set.add(refKey);
            queue.addLast(refKey);
        }

        while (!queue.isEmpty()){
            String current = queue.pollFirst();
            Node node = map.getOrDefault(current, new Node());
            for (String refKey:node.downStream.keySet()){
                if (!set.contains(refKey)){
                    set.add(refKey);
                    queue.offer(refKey);
                }else if (refKey.equals(key)){
                    return false;
                }
            }
        }
        
        return true;
    }

    private void updateValue(String key,int value){

        if (!map.containsKey(key)){
            return;
        }
        Node node = map.get(key);
        int originalValue = node.value;
        node.value += value;

        if (mode == CALCULATE_AT_SAVE){
            for (String refKey:node.upStream.keySet()){
                updateValue(refKey, node.upStream.get(refKey) * (node.value - originalValue));
            }
        }
    }
    
    class Node{
        int value;
        Map<String,Integer> downStream;
        Map<String,Integer> upStream;

        Node(){
            value = 0;
            downStream = new HashMap<>();
            upStream = new HashMap<>();
        }
    }
}
