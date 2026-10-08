package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.poi.planning.domain.model.valueobjects.TaskCode} names no activity, or a number outside 0001 to 9999. */
public final class InvalidTaskCodeException extends BusinessRuleViolationException {

    /**
     * @param rejected the code as given
     */
    public InvalidTaskCodeException(String rejected) {
        super(rejected);
    }
}
