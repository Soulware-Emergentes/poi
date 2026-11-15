package lat.soulware.poi.planning.application.commands.task.intent;

import lat.soulware.poi.planning.domain.model.valueobjects.SubmissionId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskCode;
import lat.soulware.poi.shared.application.commands.intent.CommandIntent;

/**
 * Relink a submission's delivery to the task its evidence belongs to.
 *
 * @param taskCode     the task the delivery is relinked to
 * @param fromTaskCode the task holding the delivery
 * @param submissionId the submission that made the delivery
 */
public record RelinkTaskDelivery(TaskCode taskCode, TaskCode fromTaskCode, SubmissionId submissionId)
    implements CommandIntent {
}
