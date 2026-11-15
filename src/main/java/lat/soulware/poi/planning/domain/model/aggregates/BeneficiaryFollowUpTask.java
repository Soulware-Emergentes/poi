package lat.soulware.poi.planning.domain.model.aggregates;

import java.time.Instant;
import java.util.Optional;

import lat.soulware.poi.planning.domain.model.events.TaskDefined;
import lat.soulware.poi.planning.domain.model.exceptions.MissingBeneficiaryIdException;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityCode;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.BeneficiaryId;
import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.ExecutionDate;
import lat.soulware.poi.planning.domain.model.valueobjects.SubmissionId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskCode;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskStatus;
import lat.soulware.poi.shared.domain.model.aggregates.PackagedAs;

/**
 * A follow-up visit to one beneficiary (seguimiento de beneficiarios), planned for that beneficiary.
 * Its evidence is a field sheet in sgt, whose review checks it matches the beneficiary.
 */
@PackagedAs("task")
public class BeneficiaryFollowUpTask extends Task {
    private final BeneficiaryId beneficiaryId;

    private BeneficiaryFollowUpTask(
        TaskId id,
        ActivityId activityId,
        TaskCode code,
        Description description,
        BeneficiaryId beneficiaryId,
        TaskStatus status,
        SubmissionId submissionId,
        ExecutionDate executionDate
    ) {
        super(id, activityId, code, description, status, submissionId, executionDate);
        if (beneficiaryId == null) {
            throw new MissingBeneficiaryIdException();
        }
        this.beneficiaryId = beneficiaryId;
    }

    /**
     * Defines a task under an activity, numbered right after the last task there, of any kind, or
     * 0001 when it is the first, and announces it so the plan counts the activity open until the
     * task is completed.
     *
     * @param id            the task's identity
     * @param activityId    the activity the task is planned under
     * @param activityCode  that activity's code
     * @param lastTask      the task numbered last under the activity, empty when there is none
     * @param description   what the task is
     * @param beneficiaryId the beneficiary to visit
     * @return the defined task, pending
     */
    public static BeneficiaryFollowUpTask define(
        TaskId id,
        ActivityId activityId,
        ActivityCode activityCode,
        Optional<Task> lastTask,
        Description description,
        BeneficiaryId beneficiaryId
    ) {
        TaskCode code = lastTask.map(task -> task.getCode().next())
            .orElse(new TaskCode(activityCode, 1));

        BeneficiaryFollowUpTask task = new BeneficiaryFollowUpTask(
            id,
            activityId,
            code,
            description,
            beneficiaryId,
            TaskStatus.PENDING,
            null,
            null
        );
        task.registerEvent(new TaskDefined(id, activityId, Instant.now()));
        return task;
    }

    /**
     * Rebuilds a task already defined, in whatever status it reached.
     *
     * @param id            the task's identity
     * @param activityId    the activity the task is planned under
     * @param code          the task's business identity
     * @param description   what the task is
     * @param beneficiaryId the beneficiary to visit
     * @param status        where the task stands
     * @param submissionId  the submission that made the delivery, null while it is pending
     * @param executionDate when it was carried out, null while it is pending
     * @return the task as stored
     */
    public static BeneficiaryFollowUpTask reconstitute(
        TaskId id,
        ActivityId activityId,
        TaskCode code,
        Description description,
        BeneficiaryId beneficiaryId,
        TaskStatus status,
        SubmissionId submissionId,
        ExecutionDate executionDate
    ) {
        return new BeneficiaryFollowUpTask(
            id,
            activityId,
            code,
            description,
            beneficiaryId,
            status,
            submissionId,
            executionDate
        );
    }

    /** @return the beneficiary to visit */
    public BeneficiaryId getBeneficiaryId() {
        return this.beneficiaryId;
    }
}
