package lat.soulware.poi.planning.domain.model.exceptions;

import lat.soulware.poi.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a follow-up task is defined without the beneficiary to visit. */
public final class MissingBeneficiaryIdException extends BusinessRuleViolationException {

    public MissingBeneficiaryIdException() {
        super();
    }
}
