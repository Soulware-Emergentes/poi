package lat.soulware.poi.planning.interfaces.rest.task.responses;

import java.time.LocalDate;
import java.util.UUID;

/**
 * One task, as a reviewer checks evidence against it.
 *
 * @param code          the task's code
 * @param description   what the task is
 * @param status        where the task stands
 * @param submissionId  the submission whose delivery the task holds, null while it is pending
 * @param executionDate when the delivered work was carried out, null while the task is pending
 * @param beneficiaryId the beneficiary a follow-up task is planned to visit
 */
public record TaskResponse(
    String code,
    String description,
    String status,
    UUID submissionId,
    LocalDate executionDate,
    String beneficiaryId
) {
}
