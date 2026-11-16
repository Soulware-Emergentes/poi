package lat.soulware.poi.planning.interfaces.rest.task.requests;

import java.util.UUID;

/**
 * The delivery to relink onto the task named in the path.
 *
 * @param submissionId the submission that made the delivery
 * @param fromTaskCode the task holding the delivery
 */
public record RelinkDeliveryRequest(UUID submissionId, String fromTaskCode) {
}
