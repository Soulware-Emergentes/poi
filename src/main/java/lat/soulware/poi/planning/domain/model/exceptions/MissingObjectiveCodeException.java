package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when an objective is given no code. */
public final class MissingObjectiveCodeException extends BusinessRuleViolationException {

    public MissingObjectiveCodeException() {
        super();
    }
}
