package sp_2026.airbnb.coding;

/**
 * 
Text Justification — Print as Table
Given a list of articles (or sentences) and a fixed line width, format each article into a bordered table where every line is exactly `width` characters wide. Words must not split; punctuation cannot start a line; insert a `----` separator between consecutive articles.

Requirements
Input: articles: List[str], width: int.
Each article is wrapped into lines so no line exceeds width characters and no word is split.
A line must not begin with a punctuation character (. , ; : ! ? and similar — clarify the exact set).
Avoid leaving a single word on a line when possible.
Insert a separator line "----" between consecutive articles.
For the table variant, wrap each line with | borders and pad to width with trailing spaces; horizontal rules are +---...---+.
Example (table variant, width = 55):

+-------------------------------------------------------+
| Hello world                                           |
+-------------------------------------------------------+
| How are you today                                     |
+-------------------------------------------------------+
| Bye                                                   |
+-------------------------------------------------------+
Follow-up: extend to multi-column layout — given n_columns and a per-column width, balance the article across columns.

Notes
The greedy line-builder runs in O(total characters): walk word-by-word, append until adding the next word would exceed width, then flush.
Punctuation-leading rule: if the next word starts with a punctuation char, force-attach it to the previous line even if it overflows by a small margin (clarify the exact policy — some prompts say "skip the line break", others say "always keep the punctuation with the prior word").
The "avoid a single word on a line" rule asks for a small look-ahead: if flushing now would leave a single word for the next line and that word fits with one extra space on the current line, attach it.
Border rendering is mechanical but error-prone — pre-compute the horizontal-rule string once.
The multi-column follow-up is a classic interval-balancing problem: total wrapped lines / n_columns, with overflow rules.
Preparation
Implement the wrap loop (no borders) in under 12 minutes; then layer the border rendering on top.
Hand-trace a 30-character width example with a punctuation-leading edge to confirm the policy.
Drill the multi-column follow-up: split the wrapped line list into n_columns near-equal chunks, then render side-by-side.
Practice articulating the punctuation rule before coding — interviewers often refine it mid-question.

 */
public class TextJustification {

}
