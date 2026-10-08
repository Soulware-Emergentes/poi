package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a delivery, or its review, names no submission. */
public final class MissingSubmissionIdException extends BusinessRuleViolationException {

    public MissingSubmissionIdException() {
        super();
    }
}
