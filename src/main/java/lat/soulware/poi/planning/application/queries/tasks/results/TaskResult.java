package lat.soulware.poi.planning.application.queries.tasks.results;

import java.util.UUID;

import lat.soulware.poi.shared.application.queries.results.QueryResult;

/**
 * A task as a reviewer checks evidence against it.
 *
 * @param code          the task's code, as written
 * @param description   what the task is
 * @param status        where the task stands, by name
 * @param submissionId  the submission whose delivery the task holds, null while it is pending
 * @param beneficiaryId the beneficiary a follow-up task is planned to visit
 */
public record TaskResult(
    String code,
    String description,
    String status,
    UUID submissionId,
    String beneficiaryId
) implements QueryResult {
}
