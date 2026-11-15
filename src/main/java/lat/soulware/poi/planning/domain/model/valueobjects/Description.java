package lat.soulware.poi.planning.domain.model.valueobjects;

import lat.soulware.poi.planning.domain.model.exceptions.BlankDescriptionException;
import lat.soulware.poi.shared.domain.model.valueobjects.ValueObject;

/**
 * What a plan, an objective, an activity, or a task is, in the words of whoever defines or reports it.
 *
 * @param value the text, never blank
 */
public record Description(String value) implements ValueObject {

    public Description {
        if (value == null || value.isBlank()) {
            throw new BlankDescriptionException();
        }
    }
}
