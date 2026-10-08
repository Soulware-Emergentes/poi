package lat.soulware.poi.planning.domain.repositories;

import java.util.Optional;

import lat.soulware.poi.planning.domain.model.aggregates.Task;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskCode;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.shared.domain.repositories.DomainRepository;

/** Stores {@link Task} aggregates. */
public interface TaskRepository extends DomainRepository<Task, TaskId> {

    /**
     * @param code the task's business identity
     * @return the task carrying that code, empty when none does
     */
    Optional<Task> findByCode(TaskCode code);

    /**
     * @param activityId the activity the tasks are planned under
     * @return the task numbered last under it, empty when it has none
     */
    Optional<Task> findLastUnder(ActivityId activityId);
}
