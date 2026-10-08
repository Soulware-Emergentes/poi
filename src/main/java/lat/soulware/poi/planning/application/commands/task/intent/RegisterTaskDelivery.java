package lat.soulware.poi.planning.application.commands.task.intent;

import lat.soulware.poi.planning.domain.model.valueobjects.ExecutionDate;
import lat.soulware.poi.planning.domain.model.valueobjects.SubmissionId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskCode;
import lat.soulware.poi.shared.application.commands.intent.CommandIntent;

/**
 * Register a delivery for a pending task, which takes it off the pending list until it is
 * reviewed.
 *
 * @param taskCode      the task the delivery is for
 * @param submissionId  the submission making the delivery
 * @param executionDate when the work was carried out
 */
public record RegisterTaskDelivery(
    TaskCode taskCode,
    SubmissionId submissionId,
    ExecutionDate executionDate
) implements CommandIntent {
}
