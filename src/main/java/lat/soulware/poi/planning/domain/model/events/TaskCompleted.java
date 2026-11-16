package lat.soulware.poi.planning.domain.model.events;

import java.time.Instant;

import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.shared.domain.model.events.DomainEvent;
import lat.soulware.poi.shared.domain.model.events.EmittedBy;

/**
 * A task was completed, its delivery confirmed by review.
 *
 * @param taskId     the completed task
 * @param activityId the activity it was defined under
 * @param occurredOn when the task was completed
 */
@EmittedBy("task")
public record TaskCompleted(TaskId taskId, ActivityId activityId, Instant occurredOn) implements DomainEvent {
}
