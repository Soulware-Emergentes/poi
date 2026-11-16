package lat.soulware.poi.planning.domain.model.valueobjects;

import java.time.LocalDate;

import lat.soulware.poi.planning.domain.model.exceptions.MissingExecutionDateException;
import lat.soulware.poi.shared.domain.model.valueobjects.ValueObject;

/**
 * The day the work a delivery reports was carried out.
 *
 * @param value the day, never null
 */
public record ExecutionDate(LocalDate value) implements ValueObject {

    public ExecutionDate {
        if (value == null) {
            throw new MissingExecutionDateException();
        }
    }
}
