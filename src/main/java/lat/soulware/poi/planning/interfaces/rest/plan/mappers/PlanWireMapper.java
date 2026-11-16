package lat.soulware.poi.planning.interfaces.rest.plan.mappers;

import java.util.UUID;

import lat.soulware.poi.planning.application.commands.plan.intent.DefineActivity;
import lat.soulware.poi.planning.application.commands.plan.intent.DefineObjective;
import lat.soulware.poi.planning.application.commands.plan.intent.DefinePlan;
import lat.soulware.poi.planning.application.queries.plans.criteria.ActivityById;
import lat.soulware.poi.planning.application.queries.plans.criteria.AllPlans;
import lat.soulware.poi.planning.application.queries.plans.criteria.ObjectiveById;
import lat.soulware.poi.planning.application.queries.plans.criteria.PlanById;
import lat.soulware.poi.planning.application.queries.plans.results.ActivityResult;
import lat.soulware.poi.planning.application.queries.plans.results.ObjectiveResult;
import lat.soulware.poi.planning.application.queries.plans.results.PlanResult;
import lat.soulware.poi.planning.application.queries.plans.results.PlansResult;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.MeasurementUnit;
import lat.soulware.poi.planning.domain.model.valueobjects.Name;
import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveId;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanCode;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanId;
import lat.soulware.poi.planning.domain.model.valueobjects.Target;
import lat.soulware.poi.planning.interfaces.rest.plan.requests.DefineActivityRequest;
import lat.soulware.poi.planning.interfaces.rest.plan.requests.DefineObjectiveRequest;
import lat.soulware.poi.planning.interfaces.rest.plan.requests.DefinePlanRequest;
import lat.soulware.poi.planning.interfaces.rest.plan.responses.PlansResponse;
import lat.soulware.poi.planning.interfaces.rest.plan.responses.ActivityResponse;
import lat.soulware.poi.planning.interfaces.rest.plan.responses.ObjectiveResponse;
import lat.soulware.poi.planning.interfaces.rest.plan.responses.PlanResponse;
import lat.soulware.poi.shared.interfaces.rest.mappers.WireMapper;

/** Converts what {@code PlanController} receives and answers. */
public final class PlanWireMapper implements WireMapper {

    private PlanWireMapper() {
    }

    public static DefinePlan toDefinePlan(DefinePlanRequest request) {
        return new DefinePlan(
            PlanCode.parse(request.code()),
            new Name(request.name()),
            new Description(request.description())
        );
    }

    public static DefineObjective toDefineObjective(UUID planId, UUID objectiveId, DefineObjectiveRequest request) {
        return new DefineObjective(
            new PlanId(planId),
            new ObjectiveId(objectiveId),
            new Name(request.name()),
            new Description(request.description())
        );
    }

    public static DefineActivity toDefineActivity(
        UUID planId,
        UUID objectiveId,
        UUID activityId,
        DefineActivityRequest request
    ) {
        return new DefineActivity(
            new PlanId(planId),
            new ObjectiveId(objectiveId),
            new ActivityId(activityId),
            new Name(request.name()),
            new Description(request.description()),
            new MeasurementUnit(request.measurementUnit()),
            new Target(request.target())
        );
    }

    public static PlanById toPlanById(PlanId planId) {
        return new PlanById(planId.value());
    }

    public static ObjectiveById toObjectiveById(UUID objectiveId) {
        return new ObjectiveById(objectiveId);
    }

    public static ActivityById toActivityById(UUID activityId) {
        return new ActivityById(activityId);
    }

    public static AllPlans toAllPlans() {
        return new AllPlans();
    }

    public static PlansResponse toPlansResponse(PlansResult result) {
        return new PlansResponse(result.plans().stream()
            .map(PlanWireMapper::toPlanResponse)
            .toList()
        );
    }

    public static PlanResponse toPlanResponse(PlanResult plan) {
        return new PlanResponse(
            plan.id(),
            plan.code(),
            plan.name(),
            plan.description(),
            plan.completed(),
            plan.objectives().stream()
                .map(PlanWireMapper::toObjectiveResponse)
                .toList()
        );
    }

    public static ObjectiveResponse toObjectiveResponse(ObjectiveResult objective) {
        return new ObjectiveResponse(
            objective.id(),
            objective.code(),
            objective.name(),
            objective.description(),
            objective.completed(),
            objective.activities().stream()
                .map(PlanWireMapper::toActivityResponse)
                .toList()
        );
    }

    public static ActivityResponse toActivityResponse(ActivityResult activity) {
        return new ActivityResponse(
            activity.id(),
            activity.code(),
            activity.name(),
            activity.description(),
            activity.measurementUnit(),
            activity.target(),
            activity.completed()
        );
    }
}
