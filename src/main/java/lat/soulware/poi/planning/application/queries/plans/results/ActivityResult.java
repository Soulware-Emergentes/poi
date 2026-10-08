package lat.soulware.poi.planning.application.queries.plans.results;

import java.util.UUID;

import lat.soulware.poi.shared.application.queries.results.QueryResult;

/**
 * An activity of an objective.
 *
 * @param id              the activity's identity
 * @param code            the activity's code, as written
 * @param name            what the activity is called
 * @param description     what the activity is
 * @param measurementUnit what it counts toward its target
 * @param target          how many units it sets out to deliver
 */
public record ActivityResult(
    UUID id,
    String code,
    String name,
    String description,
    String measurementUnit,
    long target
) implements QueryResult {
}
