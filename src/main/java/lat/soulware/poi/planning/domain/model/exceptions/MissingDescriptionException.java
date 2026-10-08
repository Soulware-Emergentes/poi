package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a plan, an objective, an activity, or a task is given no description. */
public final class MissingDescriptionException extends BusinessRuleViolationException {

    public MissingDescriptionException() {
        super();
    }
}
