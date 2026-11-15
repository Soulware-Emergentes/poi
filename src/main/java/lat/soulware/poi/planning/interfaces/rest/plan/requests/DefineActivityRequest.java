package lat.soulware.poi.planning.interfaces.rest.plan.requests;

/**
 * An activity to define under an objective. The objective numbers it.
 *
 * @param name            what the activity is called
 * @param description     what the activity is
 * @param measurementUnit what it counts toward its target
 * @param target          how many units it sets out to deliver
 */
public record DefineActivityRequest(String name, String description, String measurementUnit, long target) {
}
