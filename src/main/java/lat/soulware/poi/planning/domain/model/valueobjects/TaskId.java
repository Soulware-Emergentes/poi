package lat.soulware.poi.planning.domain.model.valueobjects;

import java.util.UUID;

import lat.soulware.poi.planning.domain.model.exceptions.MissingTaskIdException;
import lat.soulware.poi.shared.domain.model.valueobjects.AggregateId;

/**
 * Identity of a {@link lat.soulware.poi.planning.domain.model.aggregates.Task}.
 *
 * @param value the underlying key, never null
 */
public record TaskId(UUID value) implements AggregateId<UUID> {

    public TaskId {
        if (value == null) {
            throw new MissingTaskIdException();
        }
    }
}
