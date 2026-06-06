package sp_2026.airbnb.coding;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aho-Corasick implementation of review token labeling: wraps each occurrence as
 * {@code [label]{original_text}}. Text is normalized once (case-fold + collapse whitespace)
 * for linear multi-pattern matching; original character spans are recovered via index maps.
 */
public final class ReviewLabelerAC {

    private ReviewLabelerAC() {
    }

    public static String label(String review, Map<String, String> tokens) {
        if (review == null || review.isEmpty() || tokens == null || tokens.isEmpty()) {
            return review;
        }

        NormalizedText normalized = normalize(review);
        AhoCorasick ac = AhoCorasick.from(tokens);
        Map<Integer, List<Match>> matchesByStart = ac.findAll(normalized);

        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < review.length()) {
            Match best = longestAt(matchesByStart.get(i));
            if (best == null) {
                out.append(review.charAt(i));
                i++;
            } else {
                out.append('[').append(best.label).append("]{")
                        .append(review, best.start, best.end).append('}');
                i = best.end;
            }
        }
        return out.toString();
    }

    private static Match longestAt(List<Match> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        Match best = candidates.get(0);
        for (int k = 1; k < candidates.size(); k++) {
            Match candidate = candidates.get(k);
            if (candidate.end > best.end) {
                best = candidate;
            }
        }
        return best;
    }

    private static final class Match {
        final int start;
        final int end;
        final String label;

        Match(int start, int end, String label) {
            this.start = start;
            this.end = end;
            this.label = label;
        }
    }

    private static final class NormalizedText {
        final String text;
        final int[] origStartAt;
        final int[] origEndAt;

        NormalizedText(String text, int[] origStartAt, int[] origEndAt) {
            this.text = text;
            this.origStartAt = origStartAt;
            this.origEndAt = origEndAt;
        }
    }

    private static NormalizedText normalize(String review) {
        StringBuilder text = new StringBuilder();
        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();

        int i = 0;
        while (i < review.length()) {
            char c = review.charAt(i);
            if (Character.isWhitespace(c)) {
                int wsStart = i;
                while (i < review.length() && Character.isWhitespace(review.charAt(i))) {
                    i++;
                }
                text.append(' ');
                starts.add(wsStart);
                ends.add(i);
            } else {
                text.append(Character.toLowerCase(c));
                starts.add(i);
                ends.add(i + 1);
                i++;
            }
        }

        return new NormalizedText(
                text.toString(),
                starts.stream().mapToInt(Integer::intValue).toArray(),
                ends.stream().mapToInt(Integer::intValue).toArray());
    }

    private static String normalizeToken(String token) {
        StringBuilder text = new StringBuilder();
        int i = 0;
        while (i < token.length()) {
            char c = token.charAt(i);
            if (Character.isWhitespace(c)) {
                while (i < token.length() && Character.isWhitespace(token.charAt(i))) {
                    i++;
                }
                text.append(' ');
            } else {
                text.append(Character.toLowerCase(c));
                i++;
            }
        }
        return text.toString();
    }

    private static final class AcNode {
        final Map<Character, AcNode> next = new HashMap<>();
        AcNode fail;
        AcNode outputLink;
        String label;
        int depth;
    }

    private static final class AhoCorasick {
        private final AcNode root = new AcNode();

        static AhoCorasick from(Map<String, String> tokens) {
            AhoCorasick ac = new AhoCorasick();
            for (Map.Entry<String, String> entry : tokens.entrySet()) {
                ac.insert(normalizeToken(entry.getKey()), entry.getValue());
            }
            ac.buildFailureLinks();
            return ac;
        }

        void insert(String pattern, String label) {
            AcNode node = root;
            for (int i = 0; i < pattern.length(); i++) {
                char c = pattern.charAt(i);
                node = node.next.computeIfAbsent(c, k -> new AcNode());
                node.depth = i + 1;
            }
            node.label = label;
        }

        void buildFailureLinks() {
            Deque<AcNode> queue = new ArrayDeque<>();
            for (AcNode child : root.next.values()) {
                child.fail = root;
                queue.add(child);
            }

            while (!queue.isEmpty()) {
                AcNode current = queue.poll();
                for (Map.Entry<Character, AcNode> entry : current.next.entrySet()) {
                    char c = entry.getKey();
                    AcNode child = entry.getValue();
                    child.depth = current.depth + 1;

                    AcNode fail = current.fail;
                    while (fail != null && !fail.next.containsKey(c)) {
                        fail = fail.fail;
                    }
                    child.fail = fail == null ? root : fail.next.get(c);
                    if (child.fail == child) {
                        child.fail = root;
                    }

                    AcNode output = child.fail;
                    while (output != root && output.label == null) {
                        output = output.outputLink != null ? output.outputLink : output.fail;
                    }
                    if (output != root && output.label != null) {
                        child.outputLink = output;
                    }

                    queue.add(child);
                }
            }

            linkOutputsBfs();
        }

        private void linkOutputsBfs() {
            Deque<AcNode> queue = new ArrayDeque<>(root.next.values());
            while (!queue.isEmpty()) {
                AcNode node = queue.poll();
                if (node.label != null) {
                    continue;
                }
                AcNode cursor = node.fail;
                while (cursor != root) {
                    if (cursor.label != null) {
                        node.outputLink = cursor;
                        break;
                    }
                    cursor = cursor.outputLink != null ? cursor.outputLink : cursor.fail;
                }
                queue.addAll(node.next.values());
            }
        }

        Map<Integer, List<Match>> findAll(NormalizedText normalized) {
            Map<Integer, List<Match>> matchesByStart = new HashMap<>();
            AcNode state = root;
            String text = normalized.text;

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                while (state != root && !state.next.containsKey(c)) {
                    state = state.fail;
                }
                state = state.next.getOrDefault(c, root);

                collectMatches(state, i, normalized, matchesByStart);
            }
            return matchesByStart;
        }

        private void collectMatches(
                AcNode state,
                int normEnd,
                NormalizedText normalized,
                Map<Integer, List<Match>> matchesByStart) {
            AcNode cursor = state;
            while (cursor != root) {
                if (cursor.label != null) {
                    int normStart = normEnd - cursor.depth + 1;
                    int origStart = normalized.origStartAt[normStart];
                    int origEnd = normalized.origEndAt[normEnd];
                    Match match = new Match(origStart, origEnd, cursor.label);
                    matchesByStart.computeIfAbsent(origStart, k -> new ArrayList<>()).add(match);
                }
                cursor = cursor.outputLink != null ? cursor.outputLink : cursor.fail;
                if (cursor == state.fail && cursor.label == null) {
                    break;
                }
            }
        }
    }
}
