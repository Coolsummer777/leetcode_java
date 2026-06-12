package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class LayoverExperiencesTest {

    private final LayoverExperiences solver = new LayoverExperiences();

    private static int sumByIndices(List<Float> experiences, List<Integer> indices) {
        int sum = 0;
        for (int idx : indices) {
            sum += Math.round(experiences.get(idx) * 10);
        }
        return sum;
    }

    @Test
    void minExperiences_problemExample() {
        List<Float> experiences = List.of(3.0f, 2.0f);
        assertEquals(3, solver.minExperiences(experiences, 7.0f));
    }

    @Test
    void minExperiences_noExactFit() {
        List<Float> experiences = List.of(3.6f, 2.0f);
        assertEquals(0, solver.minExperiences(experiences, 7.5f));
    }

    @Test
    void minExperiencesV2_problemExample() {
        List<Float> experiences = List.of(3.0f, 2.0f);
        List<Integer> picked = solver.minExperiencesV2(experiences, 7.0f);

        assertEquals(3, picked.size());
        assertEquals(70, sumByIndices(experiences, picked));
    }

    @Test
    void minExperiencesV2_noExactFit() {
        assertTrue(solver.minExperiencesV2(List.of(3.6f, 2.0f), 7.5f).isEmpty());
    }

    @Test
    void minExperiencesV2_singleExperienceRepeated() {
        List<Float> experiences = List.of(2.0f);
        List<Integer> picked = solver.minExperiencesV2(experiences, 4.0f);

        assertEquals(List.of(0, 0), picked);
    }

    @Test
    void minExperiencesV2_targetZero() {
        assertTrue(solver.minExperiencesV2(List.of(2.0f, 3.0f), 0.0f).isEmpty());
    }

    @Test
    void minExperiencesV2_singlePick() {
        List<Float> experiences = List.of(1.5f, 4.0f, 2.5f);
        assertEquals(List.of(1), solver.minExperiencesV2(experiences, 4.0f));
    }

    @Test
    void minExperiencesV2_duplicateDurationsPreferSmallerIndex() {
        List<Float> experiences = List.of(2.0f, 2.0f, 3.0f);
        List<Integer> picked = solver.minExperiencesV2(experiences, 4.0f);

        assertEquals(List.of(0, 0), picked);
    }
}
