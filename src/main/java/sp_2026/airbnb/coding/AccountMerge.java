package sp_2026.airbnb.coding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
Account Merge / User Deduplication
Given a list of user records with attributes such as `name`, `email`, `phone`, mark each record as either a fresh registration or a duplicate. Two records collide if they share any attribute value across email / phone / additional identifiers; the first record wins, later collisions are flagged duplicates. Designed to extend to an arbitrary attribute set.


Requirements
Input: a list of records where each record is a uniformly-formatted dictionary like {"name": str, "email": str, "phone": str, ...}.
Walk the list in order. The first registration owns each of its attribute values; any later record that re-uses any of those values for the same attribute key is a duplicate.
Output: the indices of records that are duplicates.
Follow-up: "what if there are more attributes (region, SSN, ...)?" — the solution must work for an arbitrary, schema-driven set of attributes.
Example:

[
  {name: john,   email: john@e.com,  phone: 800 800 8800},
  {name: amy,    email: amy@e.com,   phone: 800 800 8800},  # phone clash with john
  {name: johnny, email: john@e.com,  phone: 800 800 8888},  # email clash with john
]
=> [1, 2]
Notes
Maintain one dict[attr_key, set[value]] (or per-attribute dict pointing to the owning record id). For each record, check every attribute against the corresponding "owned" set; if any value is already owned by an earlier record, flag as duplicate; otherwise register all of that record's values.
The arbitrary-attribute follow-up is the actual signal: write the loop in terms of for k, v in record.items() — do not hard-code email / phone.
Use a single union-find when the prompt extends to "merge records that transitively share any attribute": each unique (attr_key, value) is a node; for each record, union all of its (k, v) nodes; records mapping into the same component are the same user.
Edge cases: missing attribute on a record (clarify whether to skip or treat as identifier), case sensitivity on email, whitespace normalization on phone.
This is the same algorithmic skeleton as the canonical Accounts Merge problem; the Airbnb variant adds the schema-flexibility follow-up.
Preparation
Implement the linear scan with per-attribute "owned" sets in 10 minutes.
Write the union-find variant separately; be ready to switch if the interviewer broadens the question to transitive merge.
Prepare a one-liner for "data is uniformly formatted, no missing attributes" — that detail simplifies the schema-flexible loop.
Practice describing the time / space complexity in terms of record count N and attribute count A: O(N · A) with a hashmap, O(N · A · α) with union-find.




 */
public class AccountMerge {

    public static List<Integer> userDeduplication(List<List<String>> records) {

        List<Integer> res = new ArrayList<>();

        Map<String, Set<String>> infoMap = new HashMap<>();

        for (int i = 0; i < records.size(); i++) {
            List<String> record = records.get(i);
            if (!checkClash(infoMap, record)) {
                res.add(i);
                continue;
            }
            addAttribute(infoMap, record);
        }

        return res;

    }

    public static boolean checkClash(Map<String, Set<String>> infoMap, List<String> record) {
        for (String s : record) {
            String[] info = s.split(":", 2);
            String key = info[0].trim();
            String value = info[1].trim();

            Set<String> vSet = infoMap.computeIfAbsent(key, k -> new HashSet<>());
            if (vSet.contains(value)) {
                return false;
            }
        }

        return true;
    }

    public static void addAttribute(Map<String, Set<String>> infoMap, List<String> record) {
        for (String s : record) {
            String[] info = s.split(":", 2);
            String key = info[0].trim();
            String value = info[1].trim();

            Set<String> vSet = infoMap.computeIfAbsent(key, k -> new HashSet<>());
            vSet.add(value);
            infoMap.put(key, vSet);
        }
    }
    

}
