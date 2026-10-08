package lat.soulware.poi.planning.interfaces.rest.task.responses;

import java.util.List;

/**
 * Every task, ordered by code.
 *
 * @param tasks the tasks
 */
public record TasksResponse(List<TaskResponse> tasks) {
}
