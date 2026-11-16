package lat.soulware.poi.planning.application.commands.task.intent;

import lat.soulware.poi.planning.domain.model.valueobjects.SubmissionId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskCode;
import lat.soulware.poi.shared.application.commands.intent.CommandIntent;

/**
 * Complete a task, its registered delivery confirmed by review.
 *
 * @param taskCode     the task to complete
 * @param submissionId the submission that made the delivery
 */
public record CompleteTask(TaskCode taskCode, SubmissionId submissionId) implements CommandIntent {
}
