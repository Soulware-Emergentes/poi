package lat.soulware.poi.planning.domain.model.valueobjects;

import lat.soulware.poi.planning.domain.model.exceptions.BlankNameException;
import lat.soulware.poi.shared.domain.model.valueobjects.ValueObject;

/**
 * What a plan, an objective, or an activity is called, shown beside its code.
 *
 * @param value the text, never blank
 */
public record Name(String value) implements ValueObject {

    public Name {
        if (value == null || value.isBlank()) {
            throw new BlankNameException();
        }
    }
}
