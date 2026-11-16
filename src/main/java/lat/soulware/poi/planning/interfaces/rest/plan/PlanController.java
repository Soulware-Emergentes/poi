package lat.soulware.poi.planning.interfaces.rest.plan;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import lat.soulware.poi.planning.application.commands.plan.PlanCommandService;
import lat.soulware.poi.planning.application.queries.plans.PlanQueryService;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanId;
import lat.soulware.poi.planning.interfaces.rest.plan.mappers.PlanWireMapper;
import lat.soulware.poi.planning.interfaces.rest.plan.requests.DefineActivityRequest;
import lat.soulware.poi.planning.interfaces.rest.plan.requests.DefineObjectiveRequest;
import lat.soulware.poi.planning.interfaces.rest.plan.requests.DefinePlanRequest;
import lat.soulware.poi.planning.interfaces.rest.plan.responses.ActivityResponse;
import lat.soulware.poi.planning.interfaces.rest.plan.responses.ObjectiveResponse;
import lat.soulware.poi.planning.interfaces.rest.plan.responses.PlanResponse;
import lat.soulware.poi.planning.interfaces.rest.plan.responses.PlansResponse;

/**
 * The operational plans, with their objectives and activities. A plan is defined under a code no
 * other plan carries. An objective is defined under a plan and an activity under one of its
 * objectives, each numbered by the level above. Every plan is listed whole, each level by code.
 *
 * <p>Each definition answers with what it created, as read back once it is stored. A new objective
 * or activity is given its identity here, since the plan that stores it answers only with its own.
 */
@RestController
@RequestMapping("/api/v1/plans")
public class PlanController {

    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;

    public PlanController(PlanCommandService planCommandService, PlanQueryService planQueryService) {
        this.planCommandService = planCommandService;
        this.planQueryService = planQueryService;
    }

    @Operation(summary = "Returns every plan with its objectives and activities")
    @GetMapping
    public PlansResponse all() {
        return PlanWireMapper.toPlansResponse(this.planQueryService.all(PlanWireMapper.toAllPlans()));
    }

    @Operation(summary = "Defines a plan and returns it")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanResponse define(@RequestBody DefinePlanRequest request) {
        PlanId defined = this.planCommandService.define(PlanWireMapper.toDefinePlan(request));

        return PlanWireMapper.toPlanResponse(this.planQueryService.plan(PlanWireMapper.toPlanById(defined)));
    }

    @Operation(summary = "Defines an objective under a plan and returns it")
    @PostMapping("/{planId}/objectives")
    @ResponseStatus(HttpStatus.CREATED)
    public ObjectiveResponse defineObjective(@PathVariable UUID planId, @RequestBody DefineObjectiveRequest request) {
        UUID objectiveId = UUID.randomUUID();
        this.planCommandService.defineObjective(PlanWireMapper.toDefineObjective(planId, objectiveId, request));

        return PlanWireMapper.toObjectiveResponse(
            this.planQueryService.objective(PlanWireMapper.toObjectiveById(objectiveId))
        );
    }

    @Operation(summary = "Defines an activity under an objective and returns it")
    @PostMapping("/{planId}/objectives/{objectiveId}/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityResponse defineActivity(
        @PathVariable UUID planId,
        @PathVariable UUID objectiveId,
        @RequestBody DefineActivityRequest request
    ) {
        UUID activityId = UUID.randomUUID();
        this.planCommandService.defineActivity(
            PlanWireMapper.toDefineActivity(planId, objectiveId, activityId, request)
        );

        return PlanWireMapper.toActivityResponse(
            this.planQueryService.activity(PlanWireMapper.toActivityById(activityId))
        );
    }
}
