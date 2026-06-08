package sp_2026.airbnb.coding;

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

}
