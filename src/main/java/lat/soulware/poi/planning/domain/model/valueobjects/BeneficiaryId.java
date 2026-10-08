package lat.soulware.poi.planning.domain.model.valueobjects;

import lat.soulware.poi.planning.domain.model.exceptions.BlankBeneficiaryIdException;
import lat.soulware.poi.shared.domain.model.valueobjects.ValueObject;

/**
 * The identity of the beneficiary a follow-up task is planned to visit.
 *
 * @param value the beneficiary's identity, never blank
 */
public record BeneficiaryId(String value) implements ValueObject {

    public BeneficiaryId {
        if (value == null || value.isBlank()) {
            throw new BlankBeneficiaryIdException();
        }
    }
}
