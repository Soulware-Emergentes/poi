package lat.soulware.poi.planning.application.queries.tasks.results;

import java.util.List;

import lat.soulware.poi.shared.application.queries.results.QueryResult;

/**
 * Every task, ordered by code.
 *
 * @param tasks the tasks, empty when there are none
 */
public record TasksResult(List<TaskResult> tasks) implements QueryResult {

    public TasksResult {
        tasks = List.copyOf(tasks);
    }
}
