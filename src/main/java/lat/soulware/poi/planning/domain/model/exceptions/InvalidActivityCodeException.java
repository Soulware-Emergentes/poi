package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when an {@link lat.soulware.poi.planning.domain.model.valueobjects.ActivityCode} names no objective, or a number outside 01 to 99. */
public final class InvalidActivityCodeException extends BusinessRuleViolationException {

    /**
     * @param rejected the code as given
     */
    public InvalidActivityCodeException(String rejected) {
        super(rejected);
    }
}
