package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when an activity is given no code. */
public final class MissingActivityCodeException extends BusinessRuleViolationException {

    public MissingActivityCodeException() {
        super();
    }
}
