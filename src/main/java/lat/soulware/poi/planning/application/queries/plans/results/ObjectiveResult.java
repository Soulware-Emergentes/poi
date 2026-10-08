package lat.soulware.poi.planning.application.queries.plans.results;

import java.util.List;
import java.util.UUID;

import lat.soulware.poi.shared.application.queries.results.QueryResult;

/**
 * An objective of a plan, with its activities ordered by code.
 *
 * @param id          the objective's identity
 * @param code        the objective's code, as written
 * @param name        what the objective is called
 * @param description what the objective is
 * @param completed   whether it has activities and every one of them is complete
 * @param activities  the activities it is pursued through
 */
public record ObjectiveResult(
    UUID id,
    String code,
    String name,
    String description,
    boolean completed,
    List<ActivityResult> activities
) implements QueryResult {

    public ObjectiveResult {
        activities = List.copyOf(activities);
    }
}
