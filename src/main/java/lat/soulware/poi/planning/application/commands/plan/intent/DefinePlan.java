package lat.soulware.poi.planning.application.commands.plan.intent;

import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.Name;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanCode;
import lat.soulware.poi.shared.application.commands.intent.CommandIntent;

/**
 * Define an operational plan.
 *
 * @param code        the years it spans, which no other plan may carry
 * @param name        what the plan is called
 * @param description what the plan is
 */
public record DefinePlan(PlanCode code, Name name, Description description) implements CommandIntent {
}
