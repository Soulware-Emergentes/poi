package lat.soulware.poi.planning.domain.model.exceptions;

import java.util.Collection;

import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveId;
import lat.soulware.poi.shared.domain.model.exceptions.EntityNotFoundException;

/** Thrown when no objective carries the requested identity. */
public final class ObjectiveNotFoundException extends EntityNotFoundException {

    /**
     * @param missing the identities that matched nothing
     */
    public ObjectiveNotFoundException(Collection<ObjectiveId> missing) {
        super(missing);
    }
}
