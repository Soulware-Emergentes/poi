package lat.soulware.poi.planning.application.queries.plans.criteria;

import java.util.UUID;

import lat.soulware.poi.shared.application.queries.criteria.QueryCriteria;

/**
 * One activity of some plan, named by its identity.
 *
 * @param activityId the activity's identity
 */
public record ActivityById(UUID activityId) implements QueryCriteria {
}
