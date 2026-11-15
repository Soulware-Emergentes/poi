package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.poi.planning.domain.model.valueobjects.BeneficiaryId} is blank. */
public final class BlankBeneficiaryIdException extends BusinessRuleViolationException {

    public BlankBeneficiaryIdException() {
        super();
    }
}
