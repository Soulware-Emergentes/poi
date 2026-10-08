package lat.soulware.poi.planning.infrastructure.persistence.plan;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import lat.soulware.poi.planning.application.queries.plans.criteria.ActivityById;
import lat.soulware.poi.planning.application.queries.plans.criteria.AllPlans;
import lat.soulware.poi.planning.application.queries.plans.criteria.ObjectiveById;
import lat.soulware.poi.planning.application.queries.plans.criteria.PlanById;
import lat.soulware.poi.planning.application.queries.plans.projections.PlanProjection;
import lat.soulware.poi.planning.application.queries.plans.results.ActivityResult;
import lat.soulware.poi.planning.application.queries.plans.results.ObjectiveResult;
import lat.soulware.poi.planning.application.queries.plans.results.PlanResult;

/**
 * Reads plans whole, one query per level, and puts each level under its parent. A level is small
 * enough to read in full, so three queries replace one per plan and per objective. Each level query
 * narrows to one plan or one objective when asked to, which is how a single plan or objective is
 * read. A single activity is read on its own.
 */
@Component
public class JdbcPlanProjection implements PlanProjection {

    private static final String FIND_PLANS = """
        SELECT id, code, name, description
        FROM plans
        WHERE CAST(:planId AS UUID) IS NULL OR id = :planId
        ORDER BY code""";

    private static final String FIND_OBJECTIVES = """
        SELECT id, plan_id, code, name, description
        FROM objectives
        WHERE (CAST(:planId AS UUID) IS NULL OR plan_id = :planId)
            AND (CAST(:objectiveId AS UUID) IS NULL OR id = :objectiveId)
        ORDER BY code""";

    private static final String FIND_ACTIVITIES = """
        SELECT a.id, a.objective_id, a.code, a.name, a.description, a.measurement_unit, a.target
        FROM activities a
        JOIN objectives o ON o.id = a.objective_id
        WHERE (CAST(:planId AS UUID) IS NULL OR o.plan_id = :planId)
            AND (CAST(:objectiveId AS UUID) IS NULL OR a.objective_id = :objectiveId)
        ORDER BY a.code""";

    private static final String FIND_ACTIVITY = """
        SELECT id, code, name, description, measurement_unit, target
        FROM activities
        WHERE id = :activityId""";

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcPlanProjection(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<PlanResult> findAll(AllPlans criteria) {
        return this.plans(JdbcPlanProjection.narrowedTo(null, null));
    }

    @Override
    public Optional<PlanResult> findById(PlanById criteria) {
        return this.plans(JdbcPlanProjection.narrowedTo(criteria.planId(), null)).stream()
            .findFirst();
    }

    @Override
    public Optional<ObjectiveResult> findObjective(ObjectiveById criteria) {
        MapSqlParameterSource narrowed = JdbcPlanProjection.narrowedTo(null, criteria.objectiveId());

        return this.objectivesByPlan(narrowed, this.activitiesByObjective(narrowed)).values().stream()
            .flatMap(list -> list.stream())
            .findFirst();
    }

    private List<PlanResult> plans(MapSqlParameterSource narrowed) {
        Map<UUID, List<ActivityResult>> activitiesByObjective = this.activitiesByObjective(narrowed);
        Map<UUID, List<ObjectiveResult>> objectivesByPlan = this.objectivesByPlan(narrowed, activitiesByObjective);

        return this.jdbc.query(FIND_PLANS, narrowed, (row, rowNumber) -> {
            UUID id = row.getObject("id", UUID.class);

            return new PlanResult(
                id,
                row.getString("code"),
                row.getString("name"),
                row.getString("description"),
                objectivesByPlan.getOrDefault(id, List.of())
            );
        });
    }

    @Override
    public Optional<ActivityResult> findActivity(ActivityById criteria) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("activityId", criteria.activityId());

        return this.jdbc.query(FIND_ACTIVITY, parameters, (row, rowNumber) -> new ActivityResult(
                row.getObject("id", UUID.class),
                row.getString("code"),
                row.getString("name"),
                row.getString("description"),
                row.getString("measurement_unit"),
                row.getLong("target")
            ))
            .stream()
            .findFirst();
    }

    private Map<UUID, List<ObjectiveResult>> objectivesByPlan(
        MapSqlParameterSource narrowed,
        Map<UUID, List<ActivityResult>> activitiesByObjective
    ) {
        List<Child<ObjectiveResult>> objectives = this.jdbc.query(
            FIND_OBJECTIVES,
            narrowed,
            (row, rowNumber) -> {
                UUID id = row.getObject("id", UUID.class);
                ObjectiveResult objective = new ObjectiveResult(
                    id,
                    row.getString("code"),
                    row.getString("name"),
                    row.getString("description"),
                    activitiesByObjective.getOrDefault(id, List.of())
                );

                return new Child<>(row.getObject("plan_id", UUID.class), objective);
            }
        );

        return JdbcPlanProjection.byParent(objectives);
    }

    private Map<UUID, List<ActivityResult>> activitiesByObjective(MapSqlParameterSource narrowed) {
        List<Child<ActivityResult>> activities = this.jdbc.query(
            FIND_ACTIVITIES,
            narrowed,
            (row, rowNumber) -> new Child<>(row.getObject("objective_id", UUID.class), new ActivityResult(
                row.getObject("id", UUID.class),
                row.getString("code"),
                row.getString("name"),
                row.getString("description"),
                row.getString("measurement_unit"),
                row.getLong("target")
            ))
        );

        return JdbcPlanProjection.byParent(activities);
    }

    /**
     * @param planId      the plan to narrow every level to, or null for every plan
     * @param objectiveId the objective to narrow objectives and activities to, or null for every one
     * @return the parameters the level queries take
     */
    private static MapSqlParameterSource narrowedTo(UUID planId, UUID objectiveId) {
        return new MapSqlParameterSource()
            .addValue("planId", planId)
            .addValue("objectiveId", objectiveId);
    }

    /** Groups children under their parent, keeping the order they were read in. */
    private static <T> Map<UUID, List<T>> byParent(List<Child<T>> children) {
        return children.stream()
            .collect(Collectors.groupingBy(
                child -> child.parentId(),
                Collectors.mapping(child -> child.value(), Collectors.toList())
            ));
    }

    /** A row read with the id of the row it hangs from. */
    private record Child<T>(UUID parentId, T value) {
    }
}
