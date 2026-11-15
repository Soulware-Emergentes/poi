package lat.soulware.poi.planning.interfaces.rest.plan.responses;

import java.util.UUID;

/**
 * An activity of an objective.
 *
 * @param id              the activity's identity
 * @param code            the activity's code
 * @param name            what the activity is called
 * @param description     what the activity is
 * @param measurementUnit what it counts toward its target
 * @param target          how many units it sets out to deliver
 * @param completed       whether it has tasks and every one of them is completed
 */
public record ActivityResponse(
    UUID id,
    String code,
    String name,
    String description,
    String measurementUnit,
    long target,
    boolean completed
) {
}
