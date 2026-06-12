package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AccountMergeV2}: pure union-find, multiple emails per component allowed.
 */
class AccountMergeV2Test {

    private static List<List<String>> records(String[]... rows) {
        return Arrays.stream(rows).map(List::of).collect(Collectors.toList());
    }

    @Test
    void problemExample_transitiveMergeViaPhoneAndEmail() {
        List<List<String>> input = records(
                new String[] {"name:john", "email:john@e.com", "phone:800 800 8800"},
                new String[] {"name:amy", "email:amy@e.com", "phone:800 800 8800"},
                new String[] {"name:johnny", "email:john@e.com", "phone:800 800 8888"});

        assertEquals(List.of(1, 2), AccountMergeV2.userDeduplication(input));
    }

    @Test
    void transitiveMerge_threeRecordChain() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:a@e.com", "phone:2"},
                new String[] {"email:c@e.com", "phone:2"});

        assertEquals(List.of(1, 2), AccountMergeV2.userDeduplication(input));
    }

    @Test
    void differentEmailsInSameComponent_allowed() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:b@e.com", "phone:1"});

        assertEquals(List.of(1), AccountMergeV2.userDeduplication(input));
    }

    @Test
    void recordWithMultipleEmails_mergesIntoOneComponent() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "email:b@e.com", "phone:1"},
                new String[] {"phone:1"});

        assertEquals(List.of(1), AccountMergeV2.userDeduplication(input));
    }

    @Test
    void bridgeMergesTwoEmailComponents() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:b@e.com", "phone:2"},
                new String[] {"email:a@e.com", "phone:2"});

        assertEquals(List.of(1, 2), AccountMergeV2.userDeduplication(input));
    }

    @Test
    void differsFromBase_duplicateEvidenceLinksTransitively() {
        // Base version returns [1]; V2 links 0-1 via phone and 1-2 via email:x.
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:x@e.com", "phone:1"},
                new String[] {"email:x@e.com", "phone:2"});

        assertEquals(List.of(1, 2), AccountMergeV2.userDeduplication(input));
    }

    @Test
    void distinctComponents_noDuplicates() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:b@e.com", "phone:2"},
                new String[] {"email:c@e.com", "phone:3"});

        assertEquals(List.of(), AccountMergeV2.userDeduplication(input));
    }

    @Test
    void sharedEmail_marksLaterDuplicate() {
        List<List<String>> input = records(
                new String[] {"email:a@e.com", "phone:1"},
                new String[] {"email:a@e.com", "phone:2"});

        assertEquals(List.of(1), AccountMergeV2.userDeduplication(input));
    }

    @Test
    void emptyInput_returnsEmpty() {
        assertEquals(List.of(), AccountMergeV2.userDeduplication(List.of()));
    }
}
