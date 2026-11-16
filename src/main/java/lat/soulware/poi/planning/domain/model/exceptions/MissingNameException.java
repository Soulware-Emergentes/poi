package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a plan, an objective, or an activity is given no name. */
public final class MissingNameException extends BusinessRuleViolationException {

    public MissingNameException() {
        super();
    }
}
