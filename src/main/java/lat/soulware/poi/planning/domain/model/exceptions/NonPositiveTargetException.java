package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.poi.planning.domain.model.valueobjects.Target} is zero or negative. */
public final class NonPositiveTargetException extends BusinessRuleViolationException {

    /**
     * @param units the rejected count
     */
    public NonPositiveTargetException(long units) {
        super(units);
    }
}
