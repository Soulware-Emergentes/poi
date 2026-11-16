package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a task names no activity. */
public final class TaskWithoutActivityException extends BusinessRuleViolationException {

    public TaskWithoutActivityException() {
        super();
    }
}
