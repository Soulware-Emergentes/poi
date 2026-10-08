package lat.soulware.poi.planning.infrastructure.persistence.task.jpa;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** The row storing a task. */
@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    private UUID id;

    @Column(name = "activity_id", nullable = false)
    private UUID activityId;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String kind;

    @Column(nullable = false)
    private String status;

    @Column(name = "submission_id")
    private UUID submissionId;

    @Column(name = "execution_date")
    private LocalDate executionDate;

    @Column(name = "beneficiary_id")
    private String beneficiaryId;

    /** For JPA. */
    protected TaskEntity() {
    }

    public TaskEntity(
        UUID id,
        UUID activityId,
        String code,
        String description,
        String kind,
        String status,
        UUID submissionId,
        LocalDate executionDate,
        String beneficiaryId
    ) {
        this.id = id;
        this.activityId = activityId;
        this.code = code;
        this.description = description;
        this.kind = kind;
        this.status = status;
        this.submissionId = submissionId;
        this.executionDate = executionDate;
        this.beneficiaryId = beneficiaryId;
    }

    public UUID getId() {
        return this.id;
    }

    public UUID getActivityId() {
        return this.activityId;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public String getKind() {
        return this.kind;
    }

    public String getStatus() {
        return this.status;
    }

    public UUID getSubmissionId() {
        return this.submissionId;
    }

    public LocalDate getExecutionDate() {
        return this.executionDate;
    }

    public String getBeneficiaryId() {
        return this.beneficiaryId;
    }
}
