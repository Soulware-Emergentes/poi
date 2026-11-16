package lat.soulware.poi.planning.domain.model.valueobjects;

import java.util.UUID;

import lat.soulware.poi.planning.domain.model.exceptions.MissingActivityIdException;
import lat.soulware.poi.shared.domain.model.valueobjects.EntityId;

/**
 * Identity of a {@link lat.soulware.poi.planning.domain.model.entities.Activity}, unique across plans.
 *
 * @param value the underlying key, never null
 */
public record ActivityId(UUID value) implements EntityId<UUID> {

    public ActivityId {
        if (value == null) {
            throw new MissingActivityIdException();
        }
    }
}
