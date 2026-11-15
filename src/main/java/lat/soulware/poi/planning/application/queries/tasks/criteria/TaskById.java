package lat.soulware.poi.planning.application.queries.tasks.criteria;

import java.util.UUID;

import lat.soulware.poi.shared.application.queries.criteria.QueryCriteria;

/**
 * One task, named by its identity.
 *
 * @param taskId the task's identity
 */
public record TaskById(UUID taskId) implements QueryCriteria {
}
