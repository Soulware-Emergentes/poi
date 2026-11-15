package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.poi.planning.domain.model.valueobjects.ActivityId} is null. */
public final class MissingActivityIdException extends BusinessRuleViolationException {

    public MissingActivityIdException() {
        super();
    }
}
