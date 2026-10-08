package lat.soulware.poi.planning.interfaces.rest.plan.responses;

import java.util.List;
import java.util.UUID;

/**
 * A plan with the objectives it holds, ordered by code.
 *
 * @param id          the plan's identity
 * @param code        the plan's code
 * @param name        what the plan is called
 * @param description what the plan is
 * @param objectives  the objectives it holds
 */
public record PlanResponse(
    UUID id,
    String code,
    String name,
    String description,
    List<ObjectiveResponse> objectives
) {
}
