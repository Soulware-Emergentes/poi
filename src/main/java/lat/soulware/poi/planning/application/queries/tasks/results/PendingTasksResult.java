package lat.soulware.poi.planning.application.queries.tasks.results;

import java.util.List;

import lat.soulware.poi.shared.application.queries.results.QueryResult;

/**
 * The tasks waiting for a delivery, ordered by code.
 *
 * @param tasks the pending tasks, empty when none are
 */
public record PendingTasksResult(List<PendingTaskResult> tasks) implements QueryResult {

    public PendingTasksResult {
        tasks = List.copyOf(tasks);
    }
}
