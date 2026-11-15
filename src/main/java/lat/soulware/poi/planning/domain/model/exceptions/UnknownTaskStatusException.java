package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when tasks are filtered by a status no task can stand in. */
public final class UnknownTaskStatusException extends BusinessRuleViolationException {

    /**
     * @param status the status asked for
     */
    public UnknownTaskStatusException(String status) {
        super(status);
    }
}
