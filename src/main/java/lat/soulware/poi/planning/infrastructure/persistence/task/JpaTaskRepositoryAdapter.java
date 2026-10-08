package lat.soulware.poi.planning.infrastructure.persistence.task;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import lat.soulware.poi.planning.domain.model.aggregates.BeneficiaryFollowUpTask;
import lat.soulware.poi.planning.domain.model.aggregates.Task;
import lat.soulware.poi.planning.domain.model.exceptions.TaskNotFoundException;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.BeneficiaryId;
import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.ExecutionDate;
import lat.soulware.poi.planning.domain.model.valueobjects.SubmissionId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskCode;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskStatus;
import lat.soulware.poi.planning.domain.repositories.TaskRepository;
import lat.soulware.poi.planning.infrastructure.persistence.task.jpa.TaskEntity;
import lat.soulware.poi.planning.infrastructure.persistence.task.jpa.TaskJpaRepository;
import lat.soulware.poi.shared.domain.model.exceptions.EntityNotFoundException;
import lat.soulware.poi.shared.infrastructure.persistence.adapter.JpaRepositoryToDomainRepositoryAdapter;

/**
 * Stores tasks in the {@code tasks} table, the code as written and the status by name. The
 * {@code kind} column says which kind of task a row holds, so it is rebuilt as that subclass.
 */
@Repository
public class JpaTaskRepositoryAdapter
    extends JpaRepositoryToDomainRepositoryAdapter<Task, TaskId, TaskEntity, UUID>
    implements TaskRepository {

    private static final String BENEFICIARY_FOLLOW_UP = "BENEFICIARY_FOLLOW_UP";

    private final TaskJpaRepository delegate;
    private final ApplicationEventPublisher publisher;

    public JpaTaskRepositoryAdapter(TaskJpaRepository delegate, ApplicationEventPublisher publisher) {
        this.delegate = delegate;
        this.publisher = publisher;
    }

    @Override
    public Optional<Task> findByCode(TaskCode code) {
        return this.delegate.findByCode(code.value())
            .map(this::toAggregate);
    }

    @Override
    public Optional<Task> findLastUnder(ActivityId activityId) {
        return this.delegate.findFirstByActivityIdOrderByCodeDesc(activityId.value())
            .map(this::toAggregate);
    }

    @Override
    protected TaskJpaRepository delegate() {
        return this.delegate;
    }

    @Override
    protected ApplicationEventPublisher publisher() {
        return this.publisher;
    }

    @Override
    protected TaskEntity toEntity(Task aggregate) {
        if (!(aggregate instanceof BeneficiaryFollowUpTask followUp)) {
            throw new IllegalStateException("No row kind for " + aggregate.getClass().getName());
        }
        boolean isPending = followUp.getSubmissionId() == null;
        UUID submissionId = isPending
            ? null
            : followUp.getSubmissionId().value();
        LocalDate executionDate = isPending
            ? null
            : followUp.getExecutionDate().value();

        return new TaskEntity(
            followUp.getId().value(),
            followUp.getActivityId().value(),
            followUp.getCode().value(),
            followUp.getDescription().value(),
            BENEFICIARY_FOLLOW_UP,
            followUp.getStatus().name(),
            submissionId,
            executionDate,
            followUp.getBeneficiaryId().value()
        );
    }

    @Override
    protected Task toAggregate(TaskEntity entity) {
        boolean isFollowUp = BENEFICIARY_FOLLOW_UP.equals(entity.getKind());
        if (!isFollowUp) {
            throw new IllegalStateException("Unknown task kind " + entity.getKind());
        }
        boolean isPending = entity.getSubmissionId() == null;
        SubmissionId submissionId = isPending
            ? null
            : new SubmissionId(entity.getSubmissionId());
        ExecutionDate executionDate = isPending
            ? null
            : new ExecutionDate(entity.getExecutionDate());

        return BeneficiaryFollowUpTask.reconstitute(
            new TaskId(entity.getId()),
            new ActivityId(entity.getActivityId()),
            TaskCode.parse(entity.getCode()),
            new Description(entity.getDescription()),
            new BeneficiaryId(entity.getBeneficiaryId()),
            TaskStatus.valueOf(entity.getStatus()),
            submissionId,
            executionDate
        );
    }

    @Override
    protected UUID keyOf(TaskEntity entity) {
        return entity.getId();
    }

    @Override
    protected EntityNotFoundException notFound(Collection<TaskId> missing) {
        return new TaskNotFoundException(missing);
    }
}
