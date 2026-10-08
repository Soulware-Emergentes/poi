package lat.soulware.poi.planning.application.queries.plans.results;

import java.util.List;
import java.util.UUID;

import lat.soulware.poi.shared.application.queries.results.QueryResult;

/**
 * A plan with the objectives it holds, each with its activities, ordered by code.
 *
 * @param id          the plan's identity
 * @param code        the plan's code, as written
 * @param name        what the plan is called
 * @param description what the plan is
 * @param completed   whether it has objectives and every one of them is complete
 * @param objectives  the objectives it holds
 */
public record PlanResult(
    UUID id,
    String code,
    String name,
    String description,
    boolean completed,
    List<ObjectiveResult> objectives
) implements QueryResult {

    public PlanResult {
        objectives = List.copyOf(objectives);
    }
}
