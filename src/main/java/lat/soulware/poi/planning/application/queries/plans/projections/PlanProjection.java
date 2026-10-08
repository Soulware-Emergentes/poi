package lat.soulware.poi.planning.application.queries.plans.projections;

import java.util.List;
import java.util.Optional;

import lat.soulware.poi.planning.application.queries.plans.criteria.ActivityById;
import lat.soulware.poi.planning.application.queries.plans.criteria.AllPlans;
import lat.soulware.poi.planning.application.queries.plans.criteria.ObjectiveById;
import lat.soulware.poi.planning.application.queries.plans.criteria.PlanById;
import lat.soulware.poi.planning.application.queries.plans.results.ActivityResult;
import lat.soulware.poi.planning.application.queries.plans.results.ObjectiveResult;
import lat.soulware.poi.planning.application.queries.plans.results.PlanResult;
import lat.soulware.poi.shared.application.queries.projections.Projection;

/** The plans the read store holds. */
public interface PlanProjection extends Projection {

    /**
     * @param criteria which plans to read
     * @return the plans with their objectives and activities, each level ordered by code
     */
    List<PlanResult> findAll(AllPlans criteria);

    /**
     * @param criteria the plan to read
     * @return the plan with its objectives and activities, empty when no plan has that identity
     */
    Optional<PlanResult> findById(PlanById criteria);

    /**
     * @param criteria the objective to read
     * @return the objective with its activities, empty when no plan holds one with that identity
     */
    Optional<ObjectiveResult> findObjective(ObjectiveById criteria);

    /**
     * @param criteria the activity to read
     * @return the activity, empty when no plan holds one with that identity
     */
    Optional<ActivityResult> findActivity(ActivityById criteria);
}
