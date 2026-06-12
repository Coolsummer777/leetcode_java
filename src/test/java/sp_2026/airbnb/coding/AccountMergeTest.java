package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * Tests derived from the Account Merge / User Deduplication problem statement:
 * walk records in order, first registration owns each (attr_key, value) pair,
 * later records reusing any owned value under the same key are duplicates.
 */
class AccountMergeTest {

    private static List<List<String>> records(String[]... rows) {
        return Arrays.stream(rows).map(List::of).collect(Collectors.toList());
    }

    @Test
    void problemExample_phoneAndEmailClashWithFirstRecord() {
        List<List<String>> input = records(
                new String[] {"name:john", "email:john@e.com", "phone:800 800 8800"},
                new String[] {"name:amy", "email:amy@e.com", "phone:800 800 8800"},
                new String[] {"name:johnny", "email:john@e.com", "phone:800 800 8888"});

        assertEquals(List.of(1, 2), AccountMerge.userDeduplication(input));
    }

    @Test
    void emptyInput_returnsEmpty() {
        assertEquals(List.of(), AccountMerge.userDeduplication(List.of()));
    }

    @Test
    void singleRecord_returnsEmpty() {
        List<List<String>> input = records(
                new String[] {"name:alice", "email:alice@e.com", "phone:111"});

        assertEquals(List.of(), AccountMerge.userDeduplication(input));
    }

    @Test
    void allDistinctRecords_noDuplicates() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:b@e.com", "phone:2"},
                new String[] {"email:c@e.com", "phone:3"});

        assertEquals(List.of(), AccountMerge.userDeduplication(input));
    }

    @Test
    void clashOnSameAttributeKey_email() {
        List<List<String>> input = records(
                new String[] {"email:shared@e.com", "phone:1"},
                new String[] {"email:shared@e.com", "phone:2"});

        assertEquals(List.of(1), AccountMerge.userDeduplication(input));
    }

    @Test
    void clashOnSameAttributeKey_phone() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:999"},
                new String[] {"email:b@e.com", "phone:999"});

        assertEquals(List.of(1), AccountMerge.userDeduplication(input));
    }

    @Test
    void clashOnSameAttributeKey_name() {
        List<List<String>> input = records(
                new String[] {"name:bob", "email:a@e.com"},
                new String[] {"name:bob", "email:b@e.com"});

        assertEquals(List.of(1), AccountMerge.userDeduplication(input));
    }

    @Test
    void sameValueUnderDifferentKeys_doesNotClash() {
        List<List<String>> input = records(
                new String[] {"email:12345", "phone:1"},
                new String[] {"phone:12345", "email:2"});

        assertEquals(List.of(), AccountMerge.userDeduplication(input));
    }

    @Test
    void duplicateRecordDoesNotRegisterItsValues_laterRecordCanReuseThem() {
        // Record 1 is duplicate (phone clash) but its email:x must not be owned.
        // Record 2 should be fresh because x was never registered by a first-win record.
        List<List<String>> input = records(
                new String[] {"email:a", "phone:1"},
                new String[] {"email:x", "phone:1"},
                new String[] {"email:x", "phone:2"});

        assertEquals(List.of(1), AccountMerge.userDeduplication(input));
    }

    @Test
    void multipleDuplicatesOfSameFirstRecord() {
        List<List<String>> input = records(
                new String[] {"email:owner@e.com"},
                new String[] {"email:owner@e.com"},
                new String[] {"email:owner@e.com"});

        assertEquals(List.of(1, 2), AccountMerge.userDeduplication(input));
    }

    @Test
    void firstRecordWins_eachKeyTrackedIndependently() {
        List<List<String>> input = records(
                new String[] {"email:e0", "phone:p0"},
                new String[] {"email:e0", "phone:p1"},
                new String[] {"email:e1", "phone:p0"});

        assertEquals(List.of(1, 2), AccountMerge.userDeduplication(input));
    }

    @Test
    void schemaFlexible_extraAttributes_region() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "region:US"},
                new String[] {"email:b@e.com", "region:US"});

        assertEquals(List.of(1), AccountMerge.userDeduplication(input));
    }

    @Test
    void schemaFlexible_extraAttributes_ssn() {
        List<List<String>> input = records(
                new String[] {"name:ann", "ssn:111-11-1111"},
                new String[] {"name:ben", "ssn:111-11-1111"});

        assertEquals(List.of(1), AccountMerge.userDeduplication(input));
    }

    @Test
    void valueMayContainColon() {
        List<List<String>> input = records(
                new String[] {"url:https://a.com", "phone:1"},
                new String[] {"url:https://b.com", "phone:1"});

        assertEquals(List.of(1), AccountMerge.userDeduplication(input));
    }

    @Test
    void duplicateOnlyWhenSharingWithEarlierFirstWinRecord_notTransitive() {
        // Base problem: no transitive merge. Record 2 shares phone with 0 and email with 1,
        // but 1 was duplicate and never registered email:b — still direct clash on phone.
        List<List<String>> input = records(
                new String[] {"email:a", "phone:1"},
                new String[] {"email:b", "phone:2"},
                new String[] {"email:b", "phone:1"});

        assertEquals(List.of(2), AccountMerge.userDeduplication(input));
    }

    @Test
    void interleavedFreshAndDuplicateRecords() {
        List<List<String>> input = records(
                new String[] {"email:u1", "phone:100"},
                new String[] {"email:u2", "phone:200"},
                new String[] {"email:u1", "phone:200"},
                new String[] {"email:u3", "phone:100"});

        assertEquals(List.of(2, 3), AccountMerge.userDeduplication(input));
    }
}
