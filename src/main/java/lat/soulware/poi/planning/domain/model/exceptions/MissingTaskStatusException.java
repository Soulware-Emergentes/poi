package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a task is rebuilt with no status. */
public final class MissingTaskStatusException extends BusinessRuleViolationException {

    public MissingTaskStatusException() {
        super();
    }
}
