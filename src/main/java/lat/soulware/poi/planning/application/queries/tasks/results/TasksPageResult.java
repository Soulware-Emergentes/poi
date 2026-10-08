package lat.soulware.poi.planning.application.queries.tasks.results;

import java.util.List;

import lat.soulware.poi.shared.application.queries.results.QueryResult;

/**
 * One page of tasks.
 *
 * @param content       the tasks on the page, in the order asked for
 * @param totalPages    how many pages the whole selection fills
 * @param totalElements how many tasks the whole selection holds
 * @param actualPage    the page, counted from zero
 * @param pageSize      how many tasks a page holds
 */
public record TasksPageResult(
    List<TaskResult> content,
    int totalPages,
    long totalElements,
    int actualPage,
    int pageSize
) implements QueryResult {

    public TasksPageResult {
        content = List.copyOf(content);
    }
}
