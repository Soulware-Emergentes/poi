package lat.soulware.poi.planning.domain.model.valueobjects;

import java.util.UUID;

import lat.soulware.poi.planning.domain.model.exceptions.MissingSubmissionIdException;
import lat.soulware.poi.shared.domain.model.valueobjects.ValueObject;

/**
 * Identity of the submission that made a task's delivery, as the submitting system names it (a
 * field sheet's id in sgt). It tells one delivery of a task from the next.
 *
 * @param value the underlying key, never null
 */
public record SubmissionId(UUID value) implements ValueObject {

    public SubmissionId {
        if (value == null) {
            throw new MissingSubmissionIdException();
        }
    }
}
