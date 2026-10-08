package lat.soulware.poi.planning.application.queries.plans;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.queries.plans.criteria.ActivityById;
import lat.soulware.poi.planning.application.queries.plans.criteria.AllPlans;
import lat.soulware.poi.planning.application.queries.plans.criteria.ObjectiveById;
import lat.soulware.poi.planning.application.queries.plans.criteria.PlanById;
import lat.soulware.poi.planning.application.queries.plans.projections.PlanProjection;
import lat.soulware.poi.planning.application.queries.plans.results.ActivityResult;
import lat.soulware.poi.planning.application.queries.plans.results.ObjectiveResult;
import lat.soulware.poi.planning.application.queries.plans.results.PlanResult;
import lat.soulware.poi.planning.application.queries.plans.results.PlansResult;
import lat.soulware.poi.planning.domain.model.exceptions.ActivityNotFoundException;
import lat.soulware.poi.planning.domain.model.exceptions.ObjectiveNotFoundException;
import lat.soulware.poi.planning.domain.model.exceptions.PlanNotFoundException;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveId;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanId;
import lat.soulware.poi.shared.application.queries.QueryService;

/** Answers the reads about plans. */
@Service
@Transactional(readOnly = true)
public class PlanQueryService implements QueryService {

    private final PlanProjection planProjection;

    public PlanQueryService(PlanProjection planProjection) {
        this.planProjection = planProjection;
    }

    public PlansResult all(AllPlans criteria) {
        return new PlansResult(this.planProjection.findAll(criteria));
    }

    public PlanResult plan(PlanById criteria) {
        return this.planProjection.findById(criteria)
            .orElseThrow(() -> new PlanNotFoundException(List.of(new PlanId(criteria.planId()))));
    }

    public ObjectiveResult objective(ObjectiveById criteria) {
        return this.planProjection.findObjective(criteria)
            .orElseThrow(() -> new ObjectiveNotFoundException(List.of(new ObjectiveId(criteria.objectiveId()))));
    }

    public ActivityResult activity(ActivityById criteria) {
        return this.planProjection.findActivity(criteria)
            .orElseThrow(() -> new ActivityNotFoundException(List.of(new ActivityId(criteria.activityId()))));
    }
}
