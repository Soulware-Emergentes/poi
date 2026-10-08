package lat.soulware.poi.planning.domain.model.exceptions;

import java.util.Collection;

import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.shared.domain.model.exceptions.EntityNotFoundException;

/** Thrown when no task carries the requested identity. */
public final class TaskNotFoundException extends EntityNotFoundException {

    /**
     * @param missing the identities that matched nothing
     */
    public TaskNotFoundException(Collection<TaskId> missing) {
        super(missing);
    }
}
