package lat.soulware.poi.planning.interfaces.rest.task.requests;

import java.util.UUID;

/**
 * A follow-up visit to one beneficiary, to define under an activity. The activity numbers it.
 *
 * @param activityId    the activity it is planned under
 * @param description   what the task is
 * @param beneficiaryId the beneficiary to visit
 */
public record DefineBeneficiaryFollowUpTaskRequest(UUID activityId, String description, String beneficiaryId) {
}
