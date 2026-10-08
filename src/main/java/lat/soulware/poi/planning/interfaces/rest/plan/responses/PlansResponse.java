package lat.soulware.poi.planning.interfaces.rest.plan.responses;

import java.util.List;

/**
 * Every plan, ordered by code.
 *
 * @param plans the plans
 */
public record PlansResponse(List<PlanResponse> plans) {
}
