package sp_2026.airbnb.coding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Union-find variant with email as the unique identifier.
 * <p>
 * Rules:
 * <ul>
 *   <li>Records transitively linked by any shared (key, value) belong to the same user.</li>
 *   <li>Each email value may appear in at most one user component — merging two components
 *       with different emails, or a record carrying multiple emails, is rejected.</li>
 *   <li>Phone and other attributes may repeat across users and only act as linking evidence.</li>
 * </ul>
 * Output: indices of invalid records plus non-canonical records in each merged component
 * (smallest index in a component is canonical).
 */
public class AccountMergeV1 {

    private static final String EMAIL_KEY = "email";

    public static List<Integer> userDeduplication(List<List<String>> records) {
        if (records.isEmpty()) {
            return List.of();
        }

        UnionFind uf = new UnionFind();
        Map<String, Integer> nodeIds = new HashMap<>();
        Map<Integer, Set<String>> componentEmails = new HashMap<>();
        Map<Integer, List<Integer>> componentRecords = new HashMap<>();
        List<Integer> invalid = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            List<Attribute> attrs = parseRecord(records.get(i));
            if (attrs.isEmpty()) {
                componentRecords.computeIfAbsent(uf.createNode(), k -> new ArrayList<>()).add(i);
                continue;
            }

            Set<String> recordEmails = emailsIn(attrs);
            if (recordEmails.size() > 1) {
                invalid.add(i);
                continue;
            }

            List<Integer> nodes = new ArrayList<>(attrs.size());
            for (Attribute attr : attrs) {
                nodes.add(getOrCreateNode(nodeIds, uf, attr));
            }

            Set<Integer> touchedRoots = new HashSet<>();
            for (int node : nodes) {
                touchedRoots.add(uf.find(node));
            }

            Set<String> mergedEmails = new HashSet<>(recordEmails);
            for (int root : touchedRoots) {
                Set<String> existing = componentEmails.get(root);
                if (existing != null) {
                    mergedEmails.addAll(existing);
                }
            }

            if (mergedEmails.size() > 1) {
                invalid.add(i);
                continue;
            }

            int mergedRoot = nodes.get(0);
            for (int j = 1; j < nodes.size(); j++) {
                mergedRoot = uf.union(mergedRoot, nodes.get(j));
            }
            for (int root : touchedRoots) {
                mergedRoot = uf.union(mergedRoot, root);
            }
            mergedRoot = uf.find(mergedRoot);

            if (!recordEmails.isEmpty()) {
                componentEmails.computeIfAbsent(mergedRoot, k -> new HashSet<>()).addAll(recordEmails);
            } else {
                componentEmails.putIfAbsent(mergedRoot, new HashSet<>());
            }
            for (int root : touchedRoots) {
                if (root != mergedRoot) {
                    mergeEmailMetadata(componentEmails, mergedRoot, root);
                    mergeRecordIndices(componentRecords, mergedRoot, root);
                }
            }

            componentRecords.computeIfAbsent(mergedRoot, k -> new ArrayList<>()).add(i);
        }

        List<Integer> duplicates = new ArrayList<>(invalid);
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

    private static void mergeEmailMetadata(
            Map<Integer, Set<String>> componentEmails, int targetRoot, int sourceRoot) {
        Set<String> source = componentEmails.remove(sourceRoot);
        if (source == null || source.isEmpty()) {
            return;
        }
        componentEmails.computeIfAbsent(targetRoot, k -> new HashSet<>()).addAll(source);
    }

    private static void mergeRecordIndices(
            Map<Integer, List<Integer>> componentRecords, int targetRoot, int sourceRoot) {
        List<Integer> source = componentRecords.remove(sourceRoot);
        if (source == null || source.isEmpty()) {
            return;
        }
        componentRecords.computeIfAbsent(targetRoot, k -> new ArrayList<>()).addAll(source);
    }

    private static Set<String> emailsIn(List<Attribute> attrs) {
        Set<String> emails = new HashSet<>();
        for (Attribute attr : attrs) {
            if (EMAIL_KEY.equals(attr.key)) {
                emails.add(attr.value);
            }
        }
        return emails;
    }

    private static int getOrCreateNode(Map<String, Integer> nodeIds, UnionFind uf, Attribute attr) {
        String nodeKey = attr.key + ":" + attr.value;
        return nodeIds.computeIfAbsent(nodeKey, k -> uf.createNode());
    }

    private static List<Attribute> parseRecord(List<String> record) {
        List<Attribute> attrs = new ArrayList<>(record.size());
        for (String entry : record) {
            String[] parts = entry.split(":", 2);
            attrs.add(new Attribute(parts[0].trim(), parts[1].trim()));
        }
        return attrs;
    }

    private static final class Attribute {
        private final String key;
        private final String value;

        private Attribute(String key, String value) {
            this.key = key;
            this.value = value;
        }
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

        private int union(int a, int b) {
            int rootA = find(a);
            int rootB = find(b);
            if (rootA == rootB) {
                return rootA;
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
            return rootA;
        }
    }
}
