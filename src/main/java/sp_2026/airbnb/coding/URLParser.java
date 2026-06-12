package sp_2026.airbnb.coding;


import java.util.*;
/**
 * 

URL Query-String Parser
Given a URL string, parse its query portion into a dictionary. Handle keys without values, repeated keys (collect into a list), and URL-encoded characters (`%26`, `%3D`, …) via an unquote helper.


Requirements
The query string starts after the first ?.
Each parameter is key=value, separated by &.
If a key appears without =, treat the value as True (or empty string, clarify with the interviewer).
If a key appears multiple times, collect the values into a list.
Decode percent-encoded bytes — the interviewer typically hands the candidate an unquote(s) helper rather than asking them to implement it.
If the URL has no ?, return an empty dict.
Examples:

"?a=b&c=d"             -> {"a": "b", "c": "d"}
"?sd"                  -> {"sd": True}
"?a=b&a=c&a=d"         -> {"a": ["b", "c", "d"]}
"?key=hello%20world"   -> {"key": "hello world"}
Notes
Single-pass split: query = url.split("?", 1)[1] if ? exists; parts = query.split("&").
Repeated-key handling: track a dict[str, str | list[str]]; on the second occurrence promote the value to a list, on the third+ append.
Decoding policy: apply unquote to both key and value after splitting on & and = — applying it before splitting will corrupt encoded = / & inside values.
Invalid-URL handling: the interviewer typically asks for graceful behavior — drop empty segments, treat trailing & as no-op.
Clarify the no-value convention upfront (some prompts want True, others empty string, others None).
Preparation
Implement in 10 minutes cold, then layer on repeated-key and decoding.
Write 4 tests: simple, no-value key, repeated key, encoded value.
Prepare a one-liner for the follow-up "what about fragments (#section)?" — strip the fragment before parsing the query.
Pair-prep with text-justification-table — both fit the same fast phone-screen slot.


 */
public class URLParser {

    public static Map<String, List<String>> parseURL(String url) {
        Map<String, List<String>> res = new HashMap<>();

        if (url == null || url.isEmpty()) {
            return res;
        }

        if (!url.startsWith("?")){
            return res;
        }

        if (url.contains("#")) {
            url = url.split("#", 2)[0];
        }


        String[] parts = url.substring(1).split("&");
        for (String part : parts) {
            if (part.isBlank()){
                continue;
            }

            if (part.contains("=")) {
                String[] kv = part.split("=", 2);
                String key = unquote(kv[0]);
                String value = unquote(kv[1]);
                res.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            } else {
                String key = unquote(part);
                res.computeIfAbsent(key, k -> new ArrayList<>()).add("True");
            }
        }


        return res;
    }

    /** Scan for {@code %XX} and replace; {@code +} -> space. Enough for ASCII encodings in this problem. */
    public static String unquote(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '+') {
                out.append(' ');
            } else if (c == '%' && i + 2 < s.length()) {
                int hi = hexDigit(s.charAt(i + 1));
                int lo = hexDigit(s.charAt(i + 2));
                if (hi >= 0 && lo >= 0) {
                    out.append((char) ((hi << 4) | lo));
                    i += 2;
                } else {
                    out.append(c);
                }
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private static int hexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        return -1;
    }

}
