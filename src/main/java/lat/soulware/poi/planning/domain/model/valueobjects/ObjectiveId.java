package lat.soulware.poi.planning.domain.model.valueobjects;

import java.util.UUID;

import lat.soulware.poi.planning.domain.model.exceptions.MissingObjectiveIdException;
import lat.soulware.poi.shared.domain.model.valueobjects.EntityId;

/**
 * Identity of a {@link lat.soulware.poi.planning.domain.model.entities.Objective}, unique across plans.
 *
 * @param value the underlying key, never null
 */
public record ObjectiveId(UUID value) implements EntityId<UUID> {

    public ObjectiveId {
        if (value == null) {
            throw new MissingObjectiveIdException();
        }
    }
}
