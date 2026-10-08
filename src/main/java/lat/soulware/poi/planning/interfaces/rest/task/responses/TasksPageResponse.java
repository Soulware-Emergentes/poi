package lat.soulware.poi.planning.interfaces.rest.task.responses;

import java.util.List;

/**
 * One page of tasks.
 *
 * @param content       the tasks on the page, in the order asked for
 * @param totalPages    how many pages the whole selection fills
 * @param totalElements how many tasks the whole selection holds
 * @param actualPage    the page, counted from zero
 * @param pageSize      how many tasks a page holds
 */
public record TasksPageResponse(
    List<TaskResponse> content,
    int totalPages,
    long totalElements,
    int actualPage,
    int pageSize
) {
}
