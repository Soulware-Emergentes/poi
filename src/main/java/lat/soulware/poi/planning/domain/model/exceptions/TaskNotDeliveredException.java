package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.planning.domain.model.valueobjects.SubmissionId;
import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a review names a submission whose delivery the task does not hold. */
public final class TaskNotDeliveredException extends BusinessRuleViolationException {

    /**
     * @param taskCode     the code of the task
     * @param submissionId the submission the review names
     */
    public TaskNotDeliveredException(String taskCode, SubmissionId submissionId) {
        super(taskCode, submissionId.value());
    }
}
