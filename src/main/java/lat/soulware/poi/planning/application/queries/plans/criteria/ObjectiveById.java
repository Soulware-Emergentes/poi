package lat.soulware.poi.planning.application.queries.plans.criteria;

import java.util.UUID;

import lat.soulware.poi.shared.application.queries.criteria.QueryCriteria;

/**
 * One objective of some plan, named by its identity.
 *
 * @param objectiveId the objective's identity
 */
public record ObjectiveById(UUID objectiveId) implements QueryCriteria {
}
