package lat.soulware.poi.planning.application.queries.tasks.criteria;

import lat.soulware.poi.shared.application.queries.criteria.QueryCriteria;

/**
 * One task, named by its code.
 *
 * @param code the task's code, as written
 */
public record TaskByCode(String code) implements QueryCriteria {
}
