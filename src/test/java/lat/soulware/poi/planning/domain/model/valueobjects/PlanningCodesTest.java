package lat.soulware.poi.planning.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lat.soulware.poi.planning.domain.model.exceptions.InvalidActivityCodeException;
import lat.soulware.poi.planning.domain.model.exceptions.InvalidObjectiveCodeException;
import lat.soulware.poi.planning.domain.model.exceptions.InvalidPlanCodeException;
import lat.soulware.poi.planning.domain.model.exceptions.InvalidTaskCodeException;

/**
 * Covers the business codes: each one is written as its parent's code and its own number, padded,
 * reads back to the same code, and refuses a number outside its range.
 */
class PlanningCodesTest {

    private static final PlanCode PLAN = new PlanCode(2025, 2030);
    private static final ObjectiveCode OBJECTIVE = new ObjectiveCode(PLAN, 1);
    private static final ActivityCode ACTIVITY = new ActivityCode(OBJECTIVE, 3);

    @Test
    void eachCodeIsWrittenAsItsParentsCodeAndItsPaddedNumber() {
        assertEquals("2025-2030", PLAN.value());
        assertEquals("2025-2030-01", OBJECTIVE.value());
        assertEquals("2025-2030-01-03", ACTIVITY.value());
        assertEquals("2025-2030-01-03-0032", new TaskCode(ACTIVITY, 32).value());
        assertEquals("2025-2030-01-03-0230", new TaskCode(ACTIVITY, 230).value());
    }

    @Test
    void aWrittenCodeReadsBackToTheSameCode() {
        TaskCode task = new TaskCode(ACTIVITY, 32);

        assertEquals(task, TaskCode.parse("2025-2030-01-03-0032"));
        assertEquals(ACTIVITY, ActivityCode.parse("2025-2030-01-03"));
        assertEquals(PLAN, PlanCode.parse(PLAN.value()));
    }

    @Test
    void aPlanCannotEndBeforeItStarts() {
        assertThrows(InvalidPlanCodeException.class, () -> new PlanCode(2030, 2025));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2025", "25-30", "2025-2030-01", "2025/2030"})
    void aPlanCodeIsTwoFourDigitYears(String text) {
        assertThrows(InvalidPlanCodeException.class, () -> PlanCode.parse(text));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100})
    void objectivesAndActivitiesAreNumberedFrom1To99(int number) {
        assertThrows(InvalidObjectiveCodeException.class, () -> new ObjectiveCode(PLAN, number));
        assertThrows(InvalidActivityCodeException.class, () -> new ActivityCode(OBJECTIVE, number));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 10_000})
    void tasksAreNumberedFrom1To9999(int number) {
        assertThrows(InvalidTaskCodeException.class, () -> new TaskCode(ACTIVITY, number));
    }

    @Test
    void aTaskCodeNeedsAllFourDigits() {
        assertThrows(InvalidTaskCodeException.class, () -> TaskCode.parse("2025-2030-01-03-32"));
    }

    @Test
    void aCodeNamesItsParent() {
        assertThrows(InvalidObjectiveCodeException.class, () -> new ObjectiveCode(null, 1));
    }
}
