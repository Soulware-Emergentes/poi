package lat.soulware.poi.planning.interfaces.rest.plan.responses;

import java.util.List;
import java.util.UUID;

/**
 * An objective of a plan, with its activities ordered by code.
 *
 * @param id          the objective's identity
 * @param code        the objective's code
 * @param name        what the objective is called
 * @param description what the objective is
 * @param activities  the activities it is pursued through
 */
public record ObjectiveResponse(
    UUID id,
    String code,
    String name,
    String description,
    List<ActivityResponse> activities
) {
}
