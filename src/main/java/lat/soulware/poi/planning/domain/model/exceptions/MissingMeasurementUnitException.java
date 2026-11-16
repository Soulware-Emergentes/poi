package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when an activity is given no measurement unit. */
public final class MissingMeasurementUnitException extends BusinessRuleViolationException {

    public MissingMeasurementUnitException() {
        super();
    }
}
