package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a plan is given no code. */
public final class MissingPlanCodeException extends BusinessRuleViolationException {

    public MissingPlanCodeException() {
        super();
    }
}
