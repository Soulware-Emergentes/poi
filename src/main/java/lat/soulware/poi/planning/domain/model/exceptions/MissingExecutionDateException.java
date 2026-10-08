package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a task is given no execution date. */
public final class MissingExecutionDateException extends BusinessRuleViolationException {

    public MissingExecutionDateException() {
        super();
    }
}
