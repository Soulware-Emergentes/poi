package lat.soulware.poi.planning.domain.model.valueobjects;

import lat.soulware.poi.planning.domain.model.exceptions.NonPositiveTargetException;
import lat.soulware.poi.shared.domain.model.valueobjects.ValueObject;

/**
 * How many units of its measurement an activity sets out to deliver.
 *
 * @param units the count, at least one
 */
public record Target(long units) implements ValueObject {

    public Target {
        if (units < 1) {
            throw new NonPositiveTargetException(units);
        }
    }
}
