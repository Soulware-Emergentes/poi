package lat.soulware.poi.planning.application.queries.plans.criteria;

import java.util.UUID;

import lat.soulware.poi.shared.application.queries.criteria.QueryCriteria;

/**
 * One plan, named by its identity.
 *
 * @param planId the plan's identity
 */
public record PlanById(UUID planId) implements QueryCriteria {
}
