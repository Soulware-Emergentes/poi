package lat.soulware.poi.planning.domain.model.exceptions;

import java.util.Collection;

import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.shared.domain.model.exceptions.EntityNotFoundException;

/** Thrown when no activity carries the requested identity. */
public final class ActivityNotFoundException extends EntityNotFoundException {

    /**
     * @param missing the identities that matched nothing
     */
    public ActivityNotFoundException(Collection<ActivityId> missing) {
        super(missing);
    }
}
