package lat.soulware.poi.planning.domain.model.valueobjects;

import lat.soulware.poi.planning.domain.model.exceptions.BlankMeasurementUnitException;
import lat.soulware.poi.shared.domain.model.valueobjects.ValueObject;

/**
 * What an activity counts toward its target (e.g. \"beneficiarios con visita de seguimiento\"). Each task delivers one of it.
 *
 * @param value the unit, as the plan words it, never blank
 */
public record MeasurementUnit(String value) implements ValueObject {

    public MeasurementUnit {
        if (value == null || value.isBlank()) {
            throw new BlankMeasurementUnitException();
        }
    }
}
