package lat.soulware.poi.planning.application.queries.tasks.results;

import lat.soulware.poi.shared.application.queries.results.QueryResult;

/**
 * A task waiting for a delivery.
 *
 * @param code                the task's code, as written
 * @param description         what the task is
 * @param activityCode        the code of the activity it is planned under
 * @param activityDescription what that activity is
 * @param measurementUnit     what a delivery for it counts toward the activity's target
 */
public record PendingTaskResult(
    String code,
    String description,
    String activityCode,
    String activityDescription,
    String measurementUnit
) implements QueryResult {
}
