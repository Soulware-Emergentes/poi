package lat.soulware.poi.planning.application.commands.task.intent;

import lat.soulware.poi.planning.domain.model.valueobjects.SubmissionId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskCode;
import lat.soulware.poi.shared.application.commands.intent.CommandIntent;

/**
 * Reopen a task, its registered delivery rejected by review, so it is back on the pending list.
 *
 * @param taskCode     the task to reopen
 * @param submissionId the submission that made the delivery
 */
public record ReopenTask(TaskCode taskCode, SubmissionId submissionId) implements CommandIntent {
}
