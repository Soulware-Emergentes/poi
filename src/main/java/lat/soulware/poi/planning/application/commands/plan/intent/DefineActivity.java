package lat.soulware.poi.planning.application.commands.plan.intent;

import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.MeasurementUnit;
import lat.soulware.poi.planning.domain.model.valueobjects.Name;
import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveId;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanId;
import lat.soulware.poi.planning.domain.model.valueobjects.Target;
import lat.soulware.poi.shared.application.commands.intent.CommandIntent;

/**
 * Define an activity under one of a plan's objectives, which numbers it after its last one.
 *
 * @param planId          the plan holding the objective
 * @param objectiveId     the objective it pursues
 * @param activityId      the identity the new activity takes
 * @param name            what the activity is called
 * @param description     what the activity is
 * @param measurementUnit what it counts toward its target
 * @param target          how many units it sets out to deliver
 */
public record DefineActivity(
    PlanId planId,
    ObjectiveId objectiveId,
    ActivityId activityId,
    Name name,
    Description description,
    MeasurementUnit measurementUnit,
    Target target
) implements CommandIntent {
}
