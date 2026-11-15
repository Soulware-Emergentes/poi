package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when an {@link lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveCode} names no plan, or a number outside 01 to 99. */
public final class InvalidObjectiveCodeException extends BusinessRuleViolationException {

    /**
     * @param rejected the code as given
     */
    public InvalidObjectiveCodeException(String rejected) {
        super(rejected);
    }
}
