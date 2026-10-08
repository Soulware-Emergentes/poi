package lat.soulware.poi.planning.domain.model.exceptions;

import java.util.Collection;

import lat.soulware.poi.planning.domain.model.valueobjects.PlanId;
import lat.soulware.poi.shared.domain.model.exceptions.EntityNotFoundException;

/** Thrown when no plan carries the requested identity. */
public final class PlanNotFoundException extends EntityNotFoundException {

    /**
     * @param missing the identities that matched nothing
     */
    public PlanNotFoundException(Collection<PlanId> missing) {
        super(missing);
    }
}
