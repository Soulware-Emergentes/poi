package lat.soulware.poi.planning.interfaces.rest.task.responses;

/**
 * A task waiting for a delivery.
 *
 * @param code                the task's code
 * @param description         what the task is
 * @param activityCode        the code of the activity it is planned under
 * @param activityDescription what that activity is
 * @param measurementUnit     what a delivery for it counts toward the activity's target
 */
public record PendingTaskResponse(
    String code,
    String description,
    String activityCode,
    String activityDescription,
    String measurementUnit
) {
}
