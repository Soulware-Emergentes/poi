package lat.soulware.poi.planning.interfaces.rest.task.requests;

import java.util.UUID;

/**
 * Names the delivery a review settles.
 *
 * @param submissionId the submission that made the delivery
 */
public record ReviewedDeliveryRequest(UUID submissionId) {
}
