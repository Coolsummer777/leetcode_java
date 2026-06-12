package sp_2026.airbnb.coding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Union-find variant without email uniqueness constraints.
 * <p>
 * Rules:
 * <ul>
 *   <li>Each unique (key, value) pair is a node.</li>
 *   <li>All pairs in the same record are united; shared values link records transitively.</li>
 *   <li>A user component may contain multiple different emails — no conflict checks.</li>
 * </ul>
 * Output: indices of non-canonical records in each merged component (smallest index wins).
 */
public class AccountMergeV2 {

    public static List<Integer> userDeduplication(List<List<String>> records) {
        if (records.isEmpty()) {
            return List.of();
        }

        UnionFind uf = new UnionFind();
        Map<String, Integer> nodeIds = new HashMap<>();
        List<List<Integer>> recordNodes = new ArrayList<>(records.size());

        for (List<String> record : records) {
            List<Integer> nodes = new ArrayList<>(record.size());
            for (String entry : record) {
                String[] parts = entry.split(":", 2);
                String nodeKey = parts[0].trim() + ":" + parts[1].trim();
                int node = nodeIds.computeIfAbsent(nodeKey, k -> uf.createNode());
                nodes.add(node);
            }
            for (int j = 1; j < nodes.size(); j++) {
                uf.union(nodes.get(0), nodes.get(j));
            }
            recordNodes.add(nodes);
        }

        Map<Integer, List<Integer>> componentRecords = new HashMap<>();
        for (int i = 0; i < recordNodes.size(); i++) {
            List<Integer> nodes = recordNodes.get(i);
            if (nodes.isEmpty()) {
                componentRecords.computeIfAbsent(uf.createNode(), k -> new ArrayList<>()).add(i);
                continue;
            }
            int root = uf.find(nodes.get(0));
            componentRecords.computeIfAbsent(root, k -> new ArrayList<>()).add(i);
        }

        List<Integer> duplicates = new ArrayList<>();
        for (List<Integer> group : componentRecords.values()) {
            if (group.size() <= 1) {
                continue;
            }
            int canonical = group.stream().min(Integer::compareTo).orElseThrow();
            for (int index : group) {
                if (index != canonical) {
                    duplicates.add(index);
                }
            }
        }
        duplicates.sort(Integer::compareTo);
        return duplicates;
    }

    private static final class UnionFind {
        private final List<Integer> parent = new ArrayList<>();
        private final List<Integer> rank = new ArrayList<>();

        private int createNode() {
            parent.add(parent.size());
            rank.add(0);
            return parent.size() - 1;
        }

        private int find(int x) {
            if (parent.get(x) != x) {
                parent.set(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        private void union(int a, int b) {
            int rootA = find(a);
            int rootB = find(b);
            if (rootA == rootB) {
                return;
            }
            if (rank.get(rootA) < rank.get(rootB)) {
                int tmp = rootA;
                rootA = rootB;
                rootB = tmp;
            }
            parent.set(rootB, rootA);
            if (rank.get(rootA).equals(rank.get(rootB))) {
                rank.set(rootA, rank.get(rootA) + 1);
            }
        }
    }
}
