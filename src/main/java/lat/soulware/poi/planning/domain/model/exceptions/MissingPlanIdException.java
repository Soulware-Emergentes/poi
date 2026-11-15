package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.poi.planning.domain.model.valueobjects.PlanId} is null. */
public final class MissingPlanIdException extends BusinessRuleViolationException {

    public MissingPlanIdException() {
        super();
    }
}
