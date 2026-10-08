package lat.soulware.poi.planning.domain.model.events;

import java.time.Instant;

import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.shared.domain.model.events.DomainEvent;
import lat.soulware.poi.shared.domain.model.events.EmittedBy;

/**
 * A task was defined under an activity, which is not complete until that task is.
 *
 * @param taskId     the defined task
 * @param activityId the activity it was defined under
 * @param occurredOn when the task was defined
 */
@EmittedBy("task")
public record TaskDefined(TaskId taskId, ActivityId activityId, Instant occurredOn) implements DomainEvent {
}
