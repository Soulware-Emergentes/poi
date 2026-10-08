package lat.soulware.poi.planning.domain.model.valueobjects;

import java.util.UUID;

import lat.soulware.poi.planning.domain.model.exceptions.MissingPlanIdException;
import lat.soulware.poi.shared.domain.model.valueobjects.AggregateId;

/**
 * Identity of a {@link lat.soulware.poi.planning.domain.model.aggregates.Plan}.
 *
 * @param value the underlying key, never null
 */
public record PlanId(UUID value) implements AggregateId<UUID> {

    public PlanId {
        if (value == null) {
            throw new MissingPlanIdException();
        }
    }
}
