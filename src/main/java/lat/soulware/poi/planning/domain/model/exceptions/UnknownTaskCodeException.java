package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.EntityNotFoundException;

/** Thrown when no task carries the code a caller names. */
public final class UnknownTaskCodeException extends EntityNotFoundException {

    /**
     * @param taskCode the code that matched nothing
     */
    public UnknownTaskCodeException(String taskCode) {
        super(taskCode);
    }
}
