package lat.soulware.poi.planning.interfaces.rest.task.requests;

import java.time.LocalDate;
import java.util.UUID;

/**
 * The delivery a submission makes for its task.
 *
 * @param submissionId  the submission making the delivery
 * @param executionDate when the work was carried out
 */
public record RegisterDeliveryRequest(UUID submissionId, LocalDate executionDate) {
}
