package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.poi.planning.domain.model.valueobjects.PlanCode} is not two four-digit years, the second not before the first. */
public final class InvalidPlanCodeException extends BusinessRuleViolationException {

    /**
     * @param rejected the code as given
     */
    public InvalidPlanCodeException(String rejected) {
        super(rejected);
    }
}
