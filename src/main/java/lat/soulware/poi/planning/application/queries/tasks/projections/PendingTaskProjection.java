package lat.soulware.poi.planning.application.queries.tasks.projections;

import java.util.List;

import lat.soulware.poi.planning.application.queries.tasks.criteria.PendingTasks;
import lat.soulware.poi.planning.application.queries.tasks.results.PendingTaskResult;
import lat.soulware.poi.shared.application.queries.projections.Projection;

/** The pending tasks the read store holds. */
public interface PendingTaskProjection extends Projection {

    /**
     * @param criteria which pending tasks to read
     * @return the matching tasks, ordered by code
     */
    List<PendingTaskResult> findPending(PendingTasks criteria);
}
