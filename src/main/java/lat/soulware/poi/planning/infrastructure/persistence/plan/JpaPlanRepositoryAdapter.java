package lat.soulware.poi.planning.infrastructure.persistence.plan;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import lat.soulware.poi.planning.domain.model.aggregates.Plan;
import lat.soulware.poi.planning.domain.model.entities.Activity;
import lat.soulware.poi.planning.domain.model.entities.Objective;
import lat.soulware.poi.planning.domain.model.exceptions.PlanNotFoundException;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityCode;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.MeasurementUnit;
import lat.soulware.poi.planning.domain.model.valueobjects.Name;
import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveCode;
import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveId;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanCode;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanId;
import lat.soulware.poi.planning.domain.model.valueobjects.Target;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.planning.domain.repositories.PlanRepository;
import lat.soulware.poi.planning.infrastructure.persistence.plan.jpa.ActivityRow;
import lat.soulware.poi.planning.infrastructure.persistence.plan.jpa.ObjectiveRow;
import lat.soulware.poi.planning.infrastructure.persistence.plan.jpa.PlanEntity;
import lat.soulware.poi.planning.infrastructure.persistence.plan.jpa.PlanJpaRepository;
import lat.soulware.poi.shared.domain.model.exceptions.EntityNotFoundException;
import lat.soulware.poi.shared.infrastructure.persistence.adapter.JpaRepositoryToDomainRepositoryAdapter;

/**
 * Stores plans in the {@code plans} table, their objectives in {@code objectives} and the activities
 * under those in {@code activities}, each code as written, with whether each is complete. The tasks
 * an activity still has open go to {@code activity_open_tasks}. Rows are matched by id when a plan is
 * saved again, so an objective or activity defined since it was loaded is inserted and the rest are
 * left as they are.
 */
@Repository
public class JpaPlanRepositoryAdapter
    extends JpaRepositoryToDomainRepositoryAdapter<Plan, PlanId, PlanEntity, UUID>
    implements PlanRepository {

    private final PlanJpaRepository delegate;
    private final ApplicationEventPublisher publisher;

    public JpaPlanRepositoryAdapter(PlanJpaRepository delegate, ApplicationEventPublisher publisher) {
        this.delegate = delegate;
        this.publisher = publisher;
    }

    @Override
    public Optional<Plan> findByCode(PlanCode code) {
        return this.delegate.findByCode(code.value())
            .map(this::toAggregate);
    }

    @Override
    public Optional<Plan> findByActivity(ActivityId activityId) {
        return this.delegate.findByActivityId(activityId.value())
            .map(this::toAggregate);
    }

    @Override
    protected PlanJpaRepository delegate() {
        return this.delegate;
    }

    @Override
    protected ApplicationEventPublisher publisher() {
        return this.publisher;
    }

    @Override
    protected PlanEntity toEntity(Plan aggregate) {
        Set<ObjectiveRow> objectives = aggregate.getObjectives().stream()
            .map(JpaPlanRepositoryAdapter::toRow)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        return new PlanEntity(
            aggregate.getId().value(),
            aggregate.getCode().value(),
            aggregate.getName().value(),
            aggregate.getDescription().value(),
            aggregate.isComplete(),
            objectives
        );
    }

    private static ObjectiveRow toRow(Objective objective) {
        Set<ActivityRow> activities = objective.getActivities().stream()
            .map(activity -> new ActivityRow(
                activity.getId().value(),
                activity.getCode().value(),
                activity.getName().value(),
                activity.getDescription().value(),
                activity.getMeasurementUnit().value(),
                activity.getTarget().units(),
                activity.hasTasks(),
                activity.isComplete(),
                activity.getOpenTasks().stream()
                    .map(taskId -> taskId.value())
                    .collect(Collectors.toCollection(LinkedHashSet::new))
            ))
            .collect(Collectors.toCollection(LinkedHashSet::new));

        return new ObjectiveRow(
            objective.getId().value(),
            objective.getCode().value(),
            objective.getName().value(),
            objective.getDescription().value(),
            objective.isComplete(),
            activities
        );
    }

    @Override
    protected Plan toAggregate(PlanEntity entity) {
        List<Objective> objectives = entity.getObjectives().stream()
            .sorted(Comparator.comparing(objectiveRow -> objectiveRow.getCode()))
            .map(JpaPlanRepositoryAdapter::toObjective)
            .toList();

        return Plan.reconstitute(
            new PlanId(entity.getId()),
            PlanCode.parse(entity.getCode()),
            new Name(entity.getName()),
            new Description(entity.getDescription()),
            objectives
        );
    }

    private static Objective toObjective(ObjectiveRow row) {
        List<Activity> activities = row.getActivities().stream()
            .sorted(Comparator.comparing(activityRow -> activityRow.getCode()))
            .map(activity -> Activity.reconstitute(
                new ActivityId(activity.getId()),
                ActivityCode.parse(activity.getCode()),
                new Name(activity.getName()),
                new Description(activity.getDescription()),
                new MeasurementUnit(activity.getMeasurementUnit()),
                new Target(activity.getTarget()),
                activity.getOpenTasks().stream()
                    .map(taskId -> new TaskId(taskId))
                    .collect(Collectors.toSet()),
                activity.hasTasks()
            ))
            .toList();

        return Objective.reconstitute(
            new ObjectiveId(row.getId()),
            ObjectiveCode.parse(row.getCode()),
            new Name(row.getName()),
            new Description(row.getDescription()),
            activities
        );
    }

    @Override
    protected UUID keyOf(PlanEntity entity) {
        return entity.getId();
    }

    @Override
    protected EntityNotFoundException notFound(Collection<PlanId> missing) {
        return new PlanNotFoundException(missing);
    }
}
