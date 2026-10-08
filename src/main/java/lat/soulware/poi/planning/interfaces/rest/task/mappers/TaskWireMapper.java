package lat.soulware.poi.planning.interfaces.rest.task.mappers;

import lat.soulware.poi.planning.application.commands.task.intent.CompleteTask;
import lat.soulware.poi.planning.application.commands.task.intent.DefineBeneficiaryFollowUpTask;
import lat.soulware.poi.planning.application.commands.task.intent.RegisterTaskDelivery;
import lat.soulware.poi.planning.application.commands.task.intent.ReopenTask;
import lat.soulware.poi.planning.application.commands.task.intent.RelinkTaskDelivery;
import lat.soulware.poi.planning.application.queries.plans.criteria.ActivityById;
import lat.soulware.poi.planning.application.queries.plans.results.ActivityResult;
import lat.soulware.poi.planning.application.queries.tasks.criteria.PendingTasks;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskByCode;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskById;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TasksPage;
import lat.soulware.poi.planning.application.queries.tasks.results.PendingTasksResult;
import lat.soulware.poi.planning.application.queries.tasks.results.TaskResult;
import lat.soulware.poi.planning.application.queries.tasks.results.TasksPageResult;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityCode;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.BeneficiaryId;
import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.ExecutionDate;
import lat.soulware.poi.planning.domain.model.valueobjects.SubmissionId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskCode;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.planning.interfaces.rest.task.requests.DefineBeneficiaryFollowUpTaskRequest;
import lat.soulware.poi.planning.interfaces.rest.task.requests.RegisterDeliveryRequest;
import lat.soulware.poi.planning.interfaces.rest.task.requests.RelinkDeliveryRequest;
import lat.soulware.poi.planning.interfaces.rest.task.requests.ReviewedDeliveryRequest;
import lat.soulware.poi.planning.interfaces.rest.task.responses.PendingTaskResponse;
import lat.soulware.poi.planning.interfaces.rest.task.responses.PendingTasksResponse;
import lat.soulware.poi.planning.interfaces.rest.task.responses.TaskResponse;
import lat.soulware.poi.planning.interfaces.rest.task.responses.TasksPageResponse;
import lat.soulware.poi.shared.interfaces.rest.mappers.WireMapper;

/** Converts what {@code TaskController} receives and answers. */
public final class TaskWireMapper implements WireMapper {

    private TaskWireMapper() {
    }

    public static ActivityById toActivityById(DefineBeneficiaryFollowUpTaskRequest request) {
        return new ActivityById(request.activityId());
    }

    public static DefineBeneficiaryFollowUpTask toDefineBeneficiaryFollowUpTask(
        DefineBeneficiaryFollowUpTaskRequest request,
        ActivityResult activity
    ) {
        return new DefineBeneficiaryFollowUpTask(
            new ActivityId(activity.id()),
            ActivityCode.parse(activity.code()),
            new Description(request.description()),
            new BeneficiaryId(request.beneficiaryId())
        );
    }

    public static TasksPage toTasksPage(
        String status,
        String code,
        String activityCode,
        String sort,
        String direction,
        int page,
        int size
    ) {
        return new TasksPage(status, code, activityCode, sort, direction, page, size);
    }

    public static PendingTasks toPendingTasks(String activityCode) {
        return new PendingTasks(activityCode);
    }

    public static RegisterTaskDelivery toRegisterTaskDelivery(String taskCode, RegisterDeliveryRequest request) {
        return new RegisterTaskDelivery(
            TaskCode.parse(taskCode),
            new SubmissionId(request.submissionId()),
            new ExecutionDate(request.executionDate())
        );
    }

    public static CompleteTask toCompleteTask(String taskCode, ReviewedDeliveryRequest request) {
        return new CompleteTask(TaskCode.parse(taskCode), new SubmissionId(request.submissionId()));
    }

    public static ReopenTask toReopenTask(String taskCode, ReviewedDeliveryRequest request) {
        return new ReopenTask(TaskCode.parse(taskCode), new SubmissionId(request.submissionId()));
    }

    public static TaskById toTaskById(TaskId taskId) {
        return new TaskById(taskId.value());
    }

    public static TaskByCode toTaskByCode(String taskCode) {
        return new TaskByCode(taskCode);
    }

    public static RelinkTaskDelivery toRelinkTaskDelivery(String taskCode, RelinkDeliveryRequest request) {
        return new RelinkTaskDelivery(
            TaskCode.parse(taskCode),
            TaskCode.parse(request.fromTaskCode()),
            new SubmissionId(request.submissionId())
        );
    }

    public static TaskResponse toTaskResponse(TaskResult result) {
        return new TaskResponse(
            result.code(),
            result.description(),
            result.status(),
            result.submissionId(),
            result.executionDate(),
            result.beneficiaryId()
        );
    }

    public static TasksPageResponse toTasksPageResponse(TasksPageResult result) {
        return new TasksPageResponse(
            result.content().stream()
                .map(TaskWireMapper::toTaskResponse)
                .toList(),
            result.totalPages(),
            result.totalElements(),
            result.actualPage(),
            result.pageSize()
        );
    }

    public static PendingTasksResponse toPendingTasksResponse(PendingTasksResult result) {
        return new PendingTasksResponse(result.tasks().stream()
            .map(task -> new PendingTaskResponse(
                task.code(),
                task.description(),
                task.activityCode(),
                task.activityDescription(),
                task.measurementUnit()
            ))
            .toList()
        );
    }
}
