package lat.soulware.poi.planning.interfaces.rest.task.responses;

import java.util.List;

/**
 * The tasks waiting for a delivery, ordered by code.
 *
 * @param tasks the pending tasks
 */
public record PendingTasksResponse(List<PendingTaskResponse> tasks) {
}
