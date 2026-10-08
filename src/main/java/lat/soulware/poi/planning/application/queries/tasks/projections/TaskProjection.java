package lat.soulware.poi.planning.application.queries.tasks.projections;

import java.util.Optional;

import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskByCode;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskById;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TasksPage;
import lat.soulware.poi.planning.application.queries.tasks.results.TaskResult;
import lat.soulware.poi.planning.application.queries.tasks.results.TasksPageResult;
import lat.soulware.poi.shared.application.queries.projections.Projection;

/** The tasks the read store holds, one at a time or a page at a time. */
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
     * @param criteria the page to read, already validated
     * @return the page, with the size of the whole selection
     */
    TasksPageResult findPage(TasksPage criteria);
}
