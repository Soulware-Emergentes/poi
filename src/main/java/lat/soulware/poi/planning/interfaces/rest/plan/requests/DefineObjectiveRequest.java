package lat.soulware.poi.planning.interfaces.rest.plan.requests;

/**
 * An objective to define under a plan. The plan numbers it.
 *
 * @param name        what the objective is called
 * @param description what the objective is
 */
public record DefineObjectiveRequest(String name, String description) {
}
