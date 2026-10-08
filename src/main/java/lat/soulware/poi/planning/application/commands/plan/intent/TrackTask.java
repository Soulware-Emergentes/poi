package lat.soulware.poi.planning.application.commands.plan.intent;

import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.shared.application.commands.intent.CommandIntent;

/**
 * Count a task defined under an activity as open in the plan holding it.
 *
 * @param activityId the activity the task was defined under
 * @param taskId     the defined task
 */
public record TrackTask(ActivityId activityId, TaskId taskId) implements CommandIntent {
}
