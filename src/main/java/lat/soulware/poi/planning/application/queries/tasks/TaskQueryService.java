package lat.soulware.poi.planning.application.queries.tasks;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.queries.tasks.criteria.AllTasks;
import lat.soulware.poi.planning.application.queries.tasks.criteria.PendingTasks;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskByCode;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskById;
import lat.soulware.poi.planning.application.queries.tasks.projections.PendingTaskProjection;
import lat.soulware.poi.planning.application.queries.tasks.projections.TaskProjection;
import lat.soulware.poi.planning.application.queries.tasks.results.PendingTasksResult;
import lat.soulware.poi.planning.application.queries.tasks.results.TaskResult;
import lat.soulware.poi.planning.application.queries.tasks.results.TasksResult;
import lat.soulware.poi.planning.domain.model.exceptions.TaskNotFoundException;
import lat.soulware.poi.planning.domain.model.exceptions.UnknownTaskCodeException;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.shared.application.queries.QueryService;

/** Answers the reads about tasks. */
@Service
@Transactional(readOnly = true)
public class TaskQueryService implements QueryService {

    private final PendingTaskProjection pendingTaskProjection;
    private final TaskProjection taskProjection;

    public TaskQueryService(PendingTaskProjection pendingTaskProjection, TaskProjection taskProjection) {
        this.pendingTaskProjection = pendingTaskProjection;
        this.taskProjection = taskProjection;
    }

    public PendingTasksResult pending(PendingTasks criteria) {
        return new PendingTasksResult(this.pendingTaskProjection.findPending(criteria));
    }

    public TasksResult all(AllTasks criteria) {
        return new TasksResult(this.taskProjection.findAll(criteria));
    }

    public TaskResult byId(TaskById criteria) {
        return this.taskProjection.findById(criteria)
            .orElseThrow(() -> new TaskNotFoundException(List.of(new TaskId(criteria.taskId()))));
    }

    public TaskResult task(TaskByCode criteria) {
        return this.taskProjection.findByCode(criteria)
            .orElseThrow(() -> new UnknownTaskCodeException(criteria.code()));
    }
}
