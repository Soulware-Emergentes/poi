package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when an activity is given no target. */
public final class MissingTargetException extends BusinessRuleViolationException {

    public MissingTargetException() {
        super();
    }
}
