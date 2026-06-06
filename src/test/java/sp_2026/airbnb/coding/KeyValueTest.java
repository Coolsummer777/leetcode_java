package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests derived from the key-store problem statement (set_value / set_sum / get_value
 * with live sums, multiplicity, and cascade on base-key updates). Exercised via {@link KV}.
 */
class KeyValueTest {

    private KV newStore() {
        return new KeyValue2();
    }

    @Test
    void problemSample_liveSumAndCascade() {
        KV kv = newStore();

        kv.setValue("A", 5);
        kv.setValue("B", 10);
        assertTrue(kv.setRef("C", List.of("A", "B")));
        assertTrue(kv.setRef("D", List.of("C", "C", "A")));

        assertEquals(15, kv.getValue("C"));
        assertEquals(35, kv.getValue("D"));

        kv.setValue("A", 100);

        assertEquals(110, kv.getValue("C"));
        assertEquals(320, kv.getValue("D"));
    }

    @Test
    void setSum_multiplicityRepeatsContributions() {
        KV kv = newStore();

        kv.setValue("X", 3);
        assertTrue(kv.setRef("Y", List.of("X", "X", "X")));

        assertEquals(9, kv.getValue("Y"));
    }

    @Test
    void setSum_nestedDependenciesRecomputeOnBaseChange() {
        KV kv = newStore();

        kv.setValue("A", 1);
        kv.setValue("B", 2);
        assertTrue(kv.setRef("C", List.of("A", "B")));
        assertTrue(kv.setRef("D", List.of("C", "B")));

        assertEquals(3, kv.getValue("C"));
        assertEquals(5, kv.getValue("D"));

        kv.setValue("B", 20);

        assertEquals(21, kv.getValue("C"));
        assertEquals(41, kv.getValue("D"));
    }

    @Test
    void setValue_afterSetSum_replacesWithStoredInteger() {
        KV kv = newStore();

        kv.setValue("A", 5);
        kv.setValue("B", 10);
        assertTrue(kv.setRef("C", List.of("A", "B")));
        assertEquals(15, kv.getValue("C"));

        kv.setValue("C", 42);

        assertEquals(42, kv.getValue("C"));
    }

    @Test
    void setSum_afterSetValue_replacesWithLiveSum() {
        KV kv = newStore();

        kv.setValue("P", 10);
        kv.setValue("Q", 1);
        assertTrue(kv.setRef("P", List.of("Q", "Q")));

        assertEquals(2, kv.getValue("P"));
    }

    @Test
    void setSum_rejectsSelfReferenceInRefList() {
        KV kv = newStore();

        kv.setValue("S", 5);

        assertFalse(kv.setRef("T", List.of("S", "T", "S")));
        assertEquals(5, kv.getValue("S"));
    }

    @Test
    void setSum_rejectsIndirectCycle() {
        KV kv = newStore();

        kv.setValue("A", 1);
        assertTrue(kv.setRef("B", List.of("A")));

        assertFalse(kv.setRef("A", List.of("B")));
        assertEquals(1, kv.getValue("A"));
        assertEquals(1, kv.getValue("B"));
    }

    @Test
    void setSum_rejectedCycleDoesNotAlterPriorDefinition() {
        KV kv = newStore();

        kv.setValue("A", 2);
        kv.setValue("B", 3);
        assertTrue(kv.setRef("C", List.of("A", "B")));

        assertFalse(kv.setRef("A", List.of("C")));

        assertEquals(2, kv.getValue("A"));
        assertEquals(3, kv.getValue("B"));
        assertEquals(5, kv.getValue("C"));
    }

    @Test
    void setSum_emptyRefList_yieldsZero() {
        KV kv = newStore();

        kv.setValue("A", 99);
        assertTrue(kv.setRef("Z", List.of()));

        assertEquals(0, kv.getValue("Z"));
    }

    @Test
    void setValue_multipleBaseUpdates_cascadeEachTime() {
        KV kv = newStore();

        kv.setValue("A", 0);
        kv.setValue("B", 0);
        assertTrue(kv.setRef("C", List.of("A", "B")));

        kv.setValue("A", 4);
        assertEquals(4, kv.getValue("C"));

        kv.setValue("B", 10);
        assertEquals(14, kv.getValue("C"));
    }
}
