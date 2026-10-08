package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.planning.domain.model.valueobjects.TaskStatus;
import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a delivery is registered for a task that already has one, or was completed. */
public final class TaskNotPendingException extends BusinessRuleViolationException {

    /**
     * @param taskCode the code of the task
     * @param status   where the task stands
     */
    public TaskNotPendingException(String taskCode, TaskStatus status) {
        super(taskCode, status);
    }
}
