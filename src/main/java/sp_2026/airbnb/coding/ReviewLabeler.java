package sp_2026.airbnb.coding;

import java.util.HashMap;
import java.util.Map;

/**
 * Scans a review and wraps each token occurrence as {@code [label]{original_text}}.
 * Matching is case-insensitive; output preserves the original casing. Multi-word tokens
 * may span flexible whitespace in the review. When several tokens match from the same
 * offset, the longest match wins.
 */
public final class ReviewLabeler {

    private ReviewLabeler() {
    }

    public static String label(String review, Map<String, String> tokens) {
        if (review == null || review.isEmpty() || tokens == null || tokens.isEmpty()) {
            return review;
        }

        Trie trie = Trie.from(tokens);
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < review.length()) {
            Match match = trie.longestMatch(review, i);
            if (match == null) {
                out.append(review.charAt(i));
                i++;
            } else {
                out.append('[').append(match.label).append("]{")
                        .append(review, match.start, match.end).append('}');
                i = match.end;
            }
        }
        return out.toString();
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

    private static final class TrieNode {
        final Map<Character, TrieNode> children = new HashMap<>();
        TrieNode spaceChild;
        String label;
    }

    private static final class Trie {
        private final TrieNode root = new TrieNode();

        static Trie from(Map<String, String> tokens) {
            Trie trie = new Trie();
            for (Map.Entry<String, String> entry : tokens.entrySet()) {
                trie.insert(entry.getKey(), entry.getValue());
            }
            return trie;
        }

        void insert(String token, String label) {
            TrieNode node = root;
            for (int i = 0; i < token.length(); i++) {
                char c = token.charAt(i);
                if (Character.isWhitespace(c)) {
                    if (node.spaceChild == null) {
                        node.spaceChild = new TrieNode();
                    }
                    node = node.spaceChild;
                    while (i + 1 < token.length() && Character.isWhitespace(token.charAt(i + 1))) {
                        i++;
                    }
                } else {
                    char key = Character.toLowerCase(c);
                    node = node.children.computeIfAbsent(key, k -> new TrieNode());
                }
            }
            node.label = label;
        }

        Match longestMatch(String review, int start) {
            TrieNode node = root;
            int j = start;
            Integer bestEnd = null;
            String bestLabel = null;

            while (j <= review.length()) {
                if (node.label != null) {
                    bestEnd = j;
                    bestLabel = node.label;
                }
                if (j == review.length()) {
                    break;
                }

                char c = review.charAt(j);
                boolean advanced = false;

                if (!Character.isWhitespace(c)) {
                    TrieNode next = node.children.get(Character.toLowerCase(c));
                    if (next != null) {
                        node = next;
                        j++;
                        advanced = true;
                    }
                }

                if (!advanced && node.spaceChild != null && Character.isWhitespace(c)) {
                    int k = j;
                    while (k < review.length() && Character.isWhitespace(review.charAt(k))) {
                        k++;
                    }
                    if (k > j) {
                        node = node.spaceChild;
                        j = k;
                        advanced = true;
                    }
                }

                if (!advanced) {
                    break;
                }
            }

            if (bestEnd == null) {
                return null;
            }
            return new Match(start, bestEnd, bestLabel);
        }
    }
}
