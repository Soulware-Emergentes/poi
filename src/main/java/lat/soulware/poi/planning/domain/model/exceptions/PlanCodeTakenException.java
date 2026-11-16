package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a plan is defined with a code another plan already carries. */
public final class PlanCodeTakenException extends BusinessRuleViolationException {

    /**
     * @param code the code already taken
     */
    public PlanCodeTakenException(String code) {
        super(code);
    }
}
