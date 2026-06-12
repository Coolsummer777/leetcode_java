package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AccountMergeV1}: union-find with email as unique identifier.
 */
class AccountMergeV1Test {

    private static List<List<String>> records(String[]... rows) {
        return Arrays.stream(rows).map(List::of).collect(Collectors.toList());
    }

    @Test
    void sharedEmail_marksLaterRecordDuplicate() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:a@e.com", "phone:2"});

        assertEquals(List.of(1), AccountMergeV1.userDeduplication(input));
    }

    @Test
    void transitiveMerge_sameEmailChain() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:a@e.com", "phone:2"},
                new String[] {"email:a@e.com", "phone:3"});

        assertEquals(List.of(1, 2), AccountMergeV1.userDeduplication(input));
    }

    @Test
    void transitiveMerge_throughPhoneWhenEmailsCompatible() {
        // 0 and 1 share email; 1 and 2 share phone with same email family — still one email.
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:a@e.com", "phone:2"},
                new String[] {"email:a@e.com", "phone:2", "name:x"});

        assertEquals(List.of(1, 2), AccountMergeV1.userDeduplication(input));
    }

    @Test
    void bridgeDifferentEmails_rejectsBridgingRecord() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:b@e.com", "phone:2"},
                new String[] {"email:a@e.com", "phone:2"});

        assertEquals(List.of(2), AccountMergeV1.userDeduplication(input));
    }

    @Test
    void transitiveMerge_differentEmailsThroughPhone_rejectsThirdRecord() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:a@e.com", "phone:2"},
                new String[] {"email:c@e.com", "phone:2"});

        assertEquals(List.of(1, 2), AccountMergeV1.userDeduplication(input));
    }

    @Test
    void recordWithMultipleEmails_isInvalid() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "email:b@e.com", "phone:1"});

        assertEquals(List.of(0), AccountMergeV1.userDeduplication(input));
    }

    @Test
    void phoneOnlyLink_noEmailConflict_merges() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"phone:1", "name:guest"});

        assertEquals(List.of(1), AccountMergeV1.userDeduplication(input));
    }

    @Test
    void phoneOnlyComponents_mergeWhenLinkedByPhone() {
        List<List<String>> input = records(
                new String[] {"phone:1"},
                new String[] {"phone:2"},
                new String[] {"phone:1", "phone:2"});

        assertEquals(List.of(1, 2), AccountMergeV1.userDeduplication(input));
    }

    @Test
    void distinctUsers_noDuplicates() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:b@e.com", "phone:2"},
                new String[] {"email:c@e.com", "phone:3"});

        assertEquals(List.of(), AccountMergeV1.userDeduplication(input));
    }

    @Test
    void emptyInput_returnsEmpty() {
        assertEquals(List.of(), AccountMergeV1.userDeduplication(List.of()));
    }
}
