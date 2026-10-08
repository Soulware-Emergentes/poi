package lat.soulware.poi.planning.application.commands.task.intent;

import lat.soulware.poi.planning.domain.model.valueobjects.ActivityCode;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.BeneficiaryId;
import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.shared.application.commands.intent.CommandIntent;

/**
 * Define a follow-up visit to one beneficiary under an activity, numbered after the activity's last
 * task. It starts pending.
 *
 * @param activityId    the activity it is planned under
 * @param activityCode  that activity's code, which the task's code extends
 * @param description   what the task is
 * @param beneficiaryId the beneficiary to visit
 */
public record DefineBeneficiaryFollowUpTask(
    ActivityId activityId,
    ActivityCode activityCode,
    Description description,
    BeneficiaryId beneficiaryId
) implements CommandIntent {
}
