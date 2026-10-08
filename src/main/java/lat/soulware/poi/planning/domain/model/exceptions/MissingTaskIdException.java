package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.poi.planning.domain.model.valueobjects.TaskId} is null. */
public final class MissingTaskIdException extends BusinessRuleViolationException {

    public MissingTaskIdException() {
        super();
    }
}
