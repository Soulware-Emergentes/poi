package lat.soulware.poi.planning.application.commands.plan.intent;

import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.shared.application.commands.intent.CommandIntent;

/**
 * Stop counting a completed task as open in the plan holding its activity.
 *
 * @param activityId the activity the task was defined under
 * @param taskId     the completed task
 */
public record RecordTaskCompletion(ActivityId activityId, TaskId taskId) implements CommandIntent {
}
