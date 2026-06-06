package sp_2026.airbnb.coding;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Key store per problem spec: live sums with multiplicity, write-time cascade on base updates,
 * cycle rejection on setRef. Undefined keys: getValue returns null; refs in setRef must already exist.
 */
public class KeyValue2 implements KV {

    private final Map<String, Node> nodes = new HashMap<>();

    @Override
    public void setValue(String key, int value) {
        Node node = ensureNode(key);
        int delta = value - node.value;
        detachRefs(key, node);
        node.value = value;
        if (delta != 0) {
            propagateDelta(key, delta);
        }
        nodes.put(key, node);
    }

    @Override
    public boolean setRef(String key, List<String> ref) {
        if (ref == null) {
            return false;
        }
        if (!wouldBeAcyclic(key, ref)) {
            return false;
        }
        for (String r : ref) {
            if (!nodes.containsKey(r)) {
                return false;
            }
        }

        Node node = ensureNode(key);
        int oldValue = node.value;

        detachRefs(key, node);

        node.refs.clear();
        for (String r : ref) {
            node.refs.merge(r, 1, Integer::sum);
        }

        int newValue = computeSum(node.refs);
        node.value = newValue;
        attachRefs(key, node);

        int delta = newValue - oldValue;
        if (delta != 0) {
            propagateDelta(key, delta);
        }

        nodes.put(key, node);
        return true;
    }

    @Override
    public Integer getValue(String key) {
        if (!nodes.containsKey(key)) {
            return null;
        }
        return nodes.get(key).value;
    }

    private int computeSum(Map<String, Integer> refs) {
        int sum = 0;
        for (Map.Entry<String, Integer> entry : refs.entrySet()) {
            sum += nodes.get(entry.getKey()).value * entry.getValue();
        }
        return sum;
    }

    private void detachRefs(String key, Node node) {
        for (Map.Entry<String, Integer> entry : node.refs.entrySet()) {
            removeDependent(entry.getKey(), key, entry.getValue());
        }
        node.refs.clear();
    }

    private void attachRefs(String key, Node node) {
        for (Map.Entry<String, Integer> entry : node.refs.entrySet()) {
            addDependent(entry.getKey(), key, entry.getValue());
        }
    }

    private void addDependent(String refKey, String dependentKey, int count) {
        ensureNode(refKey).dependents.merge(dependentKey, count, Integer::sum);
        nodes.put(refKey, nodes.get(refKey));
    }

    private void removeDependent(String refKey, String dependentKey, int count) {
        if (!nodes.containsKey(refKey)) {
            return;
        }
        Node ref = nodes.get(refKey);
        int remaining = ref.dependents.getOrDefault(dependentKey, 0) - count;
        if (remaining <= 0) {
            ref.dependents.remove(dependentKey);
        } else {
            ref.dependents.put(dependentKey, remaining);
        }
    }

    private void propagateDelta(String key, int delta) {
        Node node = nodes.get(key);
        if (node == null || delta == 0) {
            return;
        }
        for (Map.Entry<String, Integer> entry : node.dependents.entrySet()) {
            String dependent = entry.getKey();
            int coef = entry.getValue();
            Node depNode = nodes.get(dependent);
            int old = depNode.value;
            depNode.value += coef * delta;
            propagateDelta(dependent, depNode.value - old);
        }
    }

    private boolean wouldBeAcyclic(String key, List<String> ref) {
        for (String r : ref) {
            if (r.equals(key)) {
                return false;
            }
            if (canReach(r, key)) {
                return false;
            }
        }
        return true;
    }

    private boolean canReach(String start, String target) {
        Set<String> visited = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            Node node = nodes.getOrDefault(current, new Node());
            for (String next : node.refs.keySet()) {
                if (next.equals(target)) {
                    return true;
                }
                if (visited.add(next)) {
                    queue.add(next);
                }
            }
        }
        return false;
    }

    private Node ensureNode(String key) {
        return nodes.computeIfAbsent(key, k -> new Node());
    }

    private static final class Node {
        int value;
        final Map<String, Integer> refs = new HashMap<>();
        final Map<String, Integer> dependents = new HashMap<>();
    }
}
