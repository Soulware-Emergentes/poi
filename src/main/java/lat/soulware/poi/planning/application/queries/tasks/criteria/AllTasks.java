package lat.soulware.poi.planning.application.queries.tasks.criteria;

import lat.soulware.poi.shared.application.queries.criteria.QueryCriteria;

/** Every task, in whatever status, with nothing to narrow them by. */
public record AllTasks() implements QueryCriteria {
}
