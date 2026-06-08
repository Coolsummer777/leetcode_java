package sp_2026.airbnb.coding;

import java.util.HashMap;
import java.util.Map;

/**
 *

Review — Token Tagging / Replacement
Given a review text and a map of tokens to labels, scan the text and wrap each occurrence of a token with `[label]{token}`. Case-insensitive matching; preserve the original casing in the output.

Requirements
Input:
review: string.
tokens: dict mapping token_str -> label, e.g. {"Airbnb": "business", "san francisco": "city"}.
Output: the review with each occurrence of a token (case-insensitive) wrapped as [label]{original_text}.
Multi-word tokens count as a single match across whitespace.
Example:

review = "I booked a house on Airbnb for my trip to San Francisco. It was a lovely experience."
tokens = {"Airbnb": "business", "san francisco": "city"}
=> "I booked a house on [business]{Airbnb} for my trip to [city]{San Francisco}. It was a lovely experience."
Notes
Naive for token in tokens: review = review.replace(...) fails on overlapping tokens (e.g. San Francisco Bay) and on case preservation.
The clean approach is a trie of lowercased token strings, walked over the lowercased review with a parallel cursor in the original review; on match, emit the wrapper using the original-case slice.
Aho-Corasick is the right complexity (O(|review| + total token length + #matches)) for a large token map.
Overlap policy is the clarifying question that earns points: "if Airbnb and Airbnb Inc both match starting at the same offset, which wins?" Standard answer: longest match wins.
Edge cases: token spanning punctuation, token at the start / end of the review, repeated tokens, tokens that are substrings of other tokens.
Preparation
Implement the trie-based scanner in under 25 minutes; handle case-insensitive match with original-case output.
Drill the longest-match-wins tie-breaker explicitly — it is the most common follow-up.
Pre-write the Aho-Corasick version if time allows; the interviewer asks for it when the token map is "tens of thousands".
Pair-prep with text-justification-table and url-query-string-parser since the same string-processing slot rotates among them.
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
