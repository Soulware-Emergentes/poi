package lat.soulware.poi.planning.application.queries.tasks;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.queries.tasks.criteria.PendingTasks;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskByCode;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskById;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TasksPage;
import lat.soulware.poi.planning.application.queries.tasks.projections.PendingTaskProjection;
import lat.soulware.poi.planning.application.queries.tasks.projections.TaskProjection;
import lat.soulware.poi.planning.application.queries.tasks.results.PendingTasksResult;
import lat.soulware.poi.planning.application.queries.tasks.results.TaskResult;
import lat.soulware.poi.planning.application.queries.tasks.results.TasksPageResult;
import lat.soulware.poi.planning.domain.model.exceptions.TaskNotFoundException;
import lat.soulware.poi.planning.domain.model.exceptions.UnknownTaskCodeException;
import lat.soulware.poi.planning.domain.model.exceptions.UnknownTaskStatusException;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskStatus;
import lat.soulware.poi.shared.application.queries.QueryService;
import lat.soulware.poi.shared.domain.model.exceptions.InvalidPageException;
import lat.soulware.poi.shared.domain.model.exceptions.InvalidSortDirectionException;
import lat.soulware.poi.shared.domain.model.exceptions.UnsortableColumnException;

/** Answers the reads about tasks. */
@Service
@Transactional(readOnly = true)
public class TaskQueryService implements QueryService {

    /** The columns a page of tasks sorts by, as the client names them. */
    private static final String SORTABLE = "code, status, executionDate";
    private static final Pattern SORTABLE_COLUMN = Pattern.compile("^(code|status|executionDate)$");
    private static final int MAX_PAGE_SIZE = 100;

    private final PendingTaskProjection pendingTaskProjection;
    private final TaskProjection taskProjection;

    public TaskQueryService(PendingTaskProjection pendingTaskProjection, TaskProjection taskProjection) {
        this.pendingTaskProjection = pendingTaskProjection;
        this.taskProjection = taskProjection;
    }

    public PendingTasksResult pending(PendingTasks criteria) {
        return new PendingTasksResult(this.pendingTaskProjection.findPending(criteria));
    }

    /**
     * @throws UnknownTaskStatusException    if the status names no status a task stands in
     * @throws UnsortableColumnException     if the sort names no sortable column
     * @throws InvalidSortDirectionException if the direction is neither asc nor desc
     * @throws InvalidPageException          if the page is negative or the size out of range
     */
    public TasksPageResult page(TasksPage criteria) {
        boolean namesAStatus = criteria.status() == null || Arrays.stream(TaskStatus.values())
            .anyMatch(status -> status.name().equals(criteria.status()));
        if (!namesAStatus) {
            throw new UnknownTaskStatusException(criteria.status());
        }
        boolean isSortable = criteria.sort() != null && SORTABLE_COLUMN.matcher(criteria.sort()).matches();
        if (!isSortable) {
            throw new UnsortableColumnException(criteria.sort(), SORTABLE);
        }
        boolean isADirection = "asc".equalsIgnoreCase(criteria.direction())
            || "desc".equalsIgnoreCase(criteria.direction());
        if (!isADirection) {
            throw new InvalidSortDirectionException(criteria.direction());
        }
        boolean isAPage = criteria.page() >= 0 && criteria.size() >= 1 && criteria.size() <= MAX_PAGE_SIZE;
        if (!isAPage) {
            throw new InvalidPageException(criteria.page(), criteria.size(), MAX_PAGE_SIZE);
        }

        return this.taskProjection.findPage(criteria);
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
