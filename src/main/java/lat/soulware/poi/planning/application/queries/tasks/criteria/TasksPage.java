package lat.soulware.poi.planning.application.queries.tasks.criteria;

import lat.soulware.poi.shared.application.queries.criteria.QueryCriteria;

/**
 * One page of tasks, optionally narrowed. Every filter left null matches every task.
 *
 * @param status       the status the tasks stand in, by name
 * @param code         text the task's code contains, ignoring case
 * @param activityCode the activity the tasks are planned under, as written
 * @param sort         the column to order by: {@code code}, {@code status} or {@code executionDate}
 * @param direction    {@code asc} or {@code desc}
 * @param page         the page, counted from zero
 * @param size         how many tasks a page holds
 */
public record TasksPage(
    String status,
    String code,
    String activityCode,
    String sort,
    String direction,
    int page,
    int size
) implements QueryCriteria {
}
