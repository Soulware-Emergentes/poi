package lat.soulware.poi.planning.application.commands.plan.intent;

import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.Name;
import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveId;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanId;
import lat.soulware.poi.shared.application.commands.intent.CommandIntent;

/**
 * Define an objective under a plan, which numbers it after the plan's last one.
 *
 * @param planId      the plan it belongs to
 * @param objectiveId the identity the new objective takes
 * @param name        what the objective is called
 * @param description what the objective is
 */
public record DefineObjective(
    PlanId planId,
    ObjectiveId objectiveId,
    Name name,
    Description description
) implements CommandIntent {
}
