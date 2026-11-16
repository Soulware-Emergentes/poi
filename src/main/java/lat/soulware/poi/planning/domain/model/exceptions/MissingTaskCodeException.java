package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a task is given no code. */
public final class MissingTaskCodeException extends BusinessRuleViolationException {

    public MissingTaskCodeException() {
        super();
    }
}
