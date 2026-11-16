package lat.soulware.poi.planning.application.queries.plans.results;

import java.util.List;

import lat.soulware.poi.shared.application.queries.results.QueryResult;

/**
 * Every plan, whole, ordered by code.
 *
 * @param plans the plans, empty when there are none
 */
public record PlansResult(List<PlanResult> plans) implements QueryResult {

    public PlansResult {
        plans = List.copyOf(plans);
    }
}
