package lat.soulware.poi.planning.application.queries.tasks.criteria;

import lat.soulware.poi.shared.application.queries.criteria.QueryCriteria;

/**
 * The tasks still waiting for a delivery, optionally only those under one activity.
 *
 * @param activityCode the activity to narrow to, as written, or null for every activity
 */
public record PendingTasks(String activityCode) implements QueryCriteria {
}
