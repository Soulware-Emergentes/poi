package lat.soulware.poi.planning.interfaces.rest.plan.requests;

/**
 * A plan to define.
 *
 * @param code        the years the plan spans, such as {@code 2025-2030}
 * @param name        what the plan is called
 * @param description what the plan is
 */
public record DefinePlanRequest(
    String code,
    String name,
    String description
) {
}
