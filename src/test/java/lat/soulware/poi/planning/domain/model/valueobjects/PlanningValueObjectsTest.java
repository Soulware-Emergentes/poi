package lat.soulware.poi.planning.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import lat.soulware.poi.planning.domain.model.exceptions.BlankBeneficiaryIdException;
import lat.soulware.poi.planning.domain.model.exceptions.BlankDescriptionException;
import lat.soulware.poi.planning.domain.model.exceptions.NonPositiveTargetException;

/**
 * Covers the two shapes of value the planning model guards: text that must say something, and a
 * target that must ask for at least one unit.
 */
class PlanningValueObjectsTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void aDescriptionMustSaySomething(String blank) {
        assertThrows(BlankDescriptionException.class, () -> new Description(blank));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void aBeneficiaryIdMustNameSomething(String blank) {
        assertThrows(BlankBeneficiaryIdException.class, () -> new BeneficiaryId(blank));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void aTargetAsksForAtLeastOneUnit(long units) {
        assertThrows(NonPositiveTargetException.class, () -> new Target(units));
    }

    @Test
    void aTargetOfOneUnitIsAccepted() {
        assertDoesNotThrow(() -> new Target(1));
    }

    @Test
    void beneficiaryIdsNamingTheSameBeneficiaryAreEqual() {
        assertEquals(new BeneficiaryId("DNI:12345678"), new BeneficiaryId("DNI:12345678"));
    }
}
