package lat.soulware.poi.planning.application.queries.tasks.projections;

import java.util.List;
import java.util.Optional;

import lat.soulware.poi.planning.application.queries.tasks.criteria.AllTasks;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskByCode;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskById;
import lat.soulware.poi.planning.application.queries.tasks.results.TaskResult;
import lat.soulware.poi.shared.application.queries.projections.Projection;

/** Single tasks the read store holds. */
public interface TaskProjection extends Projection {

    /**
     * @param criteria the task to read
     * @return the task, empty when no task carries that code
     */
    Optional<TaskResult> findByCode(TaskByCode criteria);

    /**
     * @param criteria the task to read
     * @return the task, empty when no task has that identity
     */
    Optional<TaskResult> findById(TaskById criteria);

    /**
     * @param criteria which tasks to read
     * @return the tasks, ordered by code
     */
    List<TaskResult> findAll(AllTasks criteria);
}
