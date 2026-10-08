package lat.soulware.poi.planning.domain.model.aggregates;

import java.time.Instant;

import lat.soulware.poi.planning.domain.model.events.TaskCompleted;
import lat.soulware.poi.planning.domain.model.exceptions.MissingDescriptionException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingExecutionDateException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingSubmissionIdException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingTaskCodeException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingTaskStatusException;
import lat.soulware.poi.planning.domain.model.exceptions.TaskNotDeliveredException;
import lat.soulware.poi.planning.domain.model.exceptions.TaskNotPendingException;
import lat.soulware.poi.planning.domain.model.exceptions.TaskWithoutActivityException;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.ExecutionDate;
import lat.soulware.poi.planning.domain.model.valueobjects.SubmissionId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskCode;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskStatus;
import lat.soulware.poi.shared.domain.model.aggregates.AggregateRoot;
import lat.soulware.poi.shared.domain.model.aggregates.PackagedAs;

/**
 * Work planned under an activity, defined ahead of anyone carrying it out. A submission registers a
 * delivery for it: which submission made it, when the work was carried out, and whatever the kind of
 * task needs to say about it. A review then confirms the delivery, which completes the task, or
 * rejects it, which leaves the task pending for another delivery.
 *
 * <p>Every step names the submission, so a step announced again changes nothing: registering or
 * confirming the delivery the task already holds is accepted as done, and rejecting a delivery the
 * task no longer holds leaves the task as it is.
 *
 * <p>Each kind of task is a subclass, which states what the task is defined for. Every kind takes its
 * delivery the same way.
 */
@PackagedAs("task")
public abstract class Task extends AggregateRoot<TaskId> {
    private final TaskId id;
    private final ActivityId activityId;
    private final TaskCode code;
    private final Description description;
    private TaskStatus status;
    private SubmissionId submissionId;
    private ExecutionDate executionDate;

    protected Task(
        TaskId id,
        ActivityId activityId,
        TaskCode code,
        Description description,
        TaskStatus status,
        SubmissionId submissionId,
        ExecutionDate executionDate
    ) {
        if (activityId == null) {
            throw new TaskWithoutActivityException();
        }
        if (code == null) {
            throw new MissingTaskCodeException();
        }
        if (description == null) {
            throw new MissingDescriptionException();
        }
        if (status == null) {
            throw new MissingTaskStatusException();
        }
        this.id = id;
        this.activityId = activityId;
        this.code = code;
        this.description = description;
        this.status = status;
        this.submissionId = submissionId;
        this.executionDate = executionDate;
    }

    /**
     * Registers a delivery for the task, which then waits for its review. A delivery the task already
     * holds is left as it is.
     *
     * @param submissionId  the submission making the delivery
     * @param executionDate when it was carried out
     * @throws TaskNotPendingException if the task already has another delivery, or was completed
     */
    public void registerDelivery(SubmissionId submissionId, ExecutionDate executionDate) {
        if (this.holdsDeliveryOf(submissionId)) {
            return;
        }
        if (this.status != TaskStatus.PENDING) {
            throw new TaskNotPendingException(this.code.value(), this.status);
        }
        if (submissionId == null) {
            throw new MissingSubmissionIdException();
        }
        if (executionDate == null) {
            throw new MissingExecutionDateException();
        }
        this.status = TaskStatus.DELIVERED;
        this.submissionId = submissionId;
        this.executionDate = executionDate;
    }

    /**
     * @param submissionId a submission
     * @return whether the task holds that submission's delivery, waiting for review or completed
     */
    private boolean holdsDeliveryOf(SubmissionId submissionId) {
        return this.status != TaskStatus.PENDING && this.submissionId.equals(submissionId);
    }

    /**
     * Completes the task, the submission's delivery confirmed by review, and announces it so the
     * plan can tell whether the task's activity is complete. A task already completed by that
     * delivery is left as it is.
     *
     * @param submissionId the submission whose delivery review confirmed
     * @throws TaskNotDeliveredException if the task does not hold that submission's delivery
     */
    public void complete(SubmissionId submissionId) {
        if (!this.holdsDeliveryOf(submissionId)) {
            throw new TaskNotDeliveredException(this.code.value(), submissionId);
        }
        if (this.status == TaskStatus.COMPLETED) {
            return;
        }
        this.status = TaskStatus.COMPLETED;
        this.registerEvent(new TaskCompleted(this.id, this.activityId, Instant.now()));
    }

    /**
     * Reopens the task, the submission's delivery rejected by review. The task returns to pending,
     * without the delivery, so another one can be registered. A delivery the task no longer holds was already
     * rejected, so the task is left as it is.
     *
     * @param submissionId the submission whose delivery review rejected
     * @throws TaskNotDeliveredException if that submission's delivery already completed the task
     */
    public void reopen(SubmissionId submissionId) {
        if (!this.holdsDeliveryOf(submissionId)) {
            return;
        }
        if (this.status == TaskStatus.COMPLETED) {
            throw new TaskNotDeliveredException(this.code.value(), submissionId);
        }
        this.release();
    }

    /**
     * Takes over the submission's delivery from the task it was registered for, because the review
     * found the evidence belongs to this one. A pending task takes the delivery and the source returns
     * to pending. A task waiting for review of another delivery swaps it with the source, so neither
     * submission loses its task. A relink already done changes nothing.
     *
     * @param source       the task holding the submission's delivery
     * @param submissionId the submission being relinked
     * @throws TaskNotDeliveredException if the source does not hold that submission's delivery
     *                                   waiting for review
     * @throws TaskNotPendingException   if this task was completed
     */
    public void relinkDeliveryFrom(Task source, SubmissionId submissionId) {
        if (this.holdsDeliveryOf(submissionId)) {
            return;
        }
        boolean sourceAwaitsReview = source.status == TaskStatus.DELIVERED && source.holdsDeliveryOf(submissionId);
        if (!sourceAwaitsReview) {
            throw new TaskNotDeliveredException(source.code.value(), submissionId);
        }
        if (this.status == TaskStatus.COMPLETED) {
            throw new TaskNotPendingException(this.code.value(), this.status);
        }

        ExecutionDate executionDate = source.executionDate;
        source.release();
        if (this.status == TaskStatus.PENDING) {
            this.registerDelivery(submissionId, executionDate);
            return;
        }

        SubmissionId other = this.submissionId;
        ExecutionDate otherExecutionDate = this.executionDate;
        this.release();
        this.registerDelivery(submissionId, executionDate);
        source.registerDelivery(other, otherExecutionDate);
    }

    private void release() {
        this.status = TaskStatus.PENDING;
        this.submissionId = null;
        this.executionDate = null;
    }

    @Override
    public TaskId getId() {
        return this.id;
    }

    public ActivityId getActivityId() {
        return this.activityId;
    }

    public TaskCode getCode() {
        return this.code;
    }

    public Description getDescription() {
        return this.description;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    /** @return the submission that made the delivery, null while the task is pending */
    public SubmissionId getSubmissionId() {
        return this.submissionId;
    }

    /** @return when it was carried out, null while it is pending */
    public ExecutionDate getExecutionDate() {
        return this.executionDate;
    }
}
