package lat.soulware.poi.planning.interfaces.rest.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import lat.soulware.poi.planning.application.commands.task.TaskCommandService;
import lat.soulware.poi.planning.application.queries.plans.PlanQueryService;
import lat.soulware.poi.planning.application.queries.tasks.TaskQueryService;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.planning.interfaces.rest.task.mappers.TaskWireMapper;
import lat.soulware.poi.planning.interfaces.rest.task.requests.DefineBeneficiaryFollowUpTaskRequest;
import lat.soulware.poi.planning.interfaces.rest.task.requests.RegisterDeliveryRequest;
import lat.soulware.poi.planning.interfaces.rest.task.requests.RelinkDeliveryRequest;
import lat.soulware.poi.planning.interfaces.rest.task.requests.ReviewedDeliveryRequest;
import lat.soulware.poi.planning.interfaces.rest.task.responses.PendingTasksResponse;
import lat.soulware.poi.planning.interfaces.rest.task.responses.TaskResponse;
import lat.soulware.poi.planning.interfaces.rest.task.responses.TasksResponse;

/**
 * The tasks of the operational plan. People define them, one follow-up visit at a time under an
 * activity, whose code is read from the plans before the task is numbered after it, and list them
 * all.
 *
 * <p>Other systems deliver them. A delivery is registered when
 * work is submitted, which takes the task off the pending list, and reviewed later: confirmed, which
 * completes the task, or rejected, which puts it back. Each names the submission that made the
 * delivery, so a request sent again changes nothing. A delivery can also be relinked to the task its
 * evidence belongs to, swapping with the delivery that task held; the answer is the task the delivery
 * left, which names the swapped submission when there was one.
 *
 * <p>Defining answers with the task it created, and registering, confirming and rejecting a delivery
 * with the task as it stands afterward.
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskCommandService taskCommandService;
    private final TaskQueryService taskQueryService;
    private final PlanQueryService planQueryService;

    public TaskController(
        TaskCommandService taskCommandService,
        TaskQueryService taskQueryService,
        PlanQueryService planQueryService
    ) {
        this.taskCommandService = taskCommandService;
        this.taskQueryService = taskQueryService;
        this.planQueryService = planQueryService;
    }

    @Operation(summary = "Returns every task")
    @GetMapping
    public TasksResponse all() {
        return TaskWireMapper.toTasksResponse(this.taskQueryService.all(TaskWireMapper.toAllTasks()));
    }

    @Operation(summary = "Defines a beneficiary follow-up task under an activity and returns it")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse defineBeneficiaryFollowUp(@RequestBody DefineBeneficiaryFollowUpTaskRequest request) {
        TaskId defined = this.taskCommandService.defineBeneficiaryFollowUp(
            TaskWireMapper.toDefineBeneficiaryFollowUpTask(
            request,
            this.planQueryService.activity(TaskWireMapper.toActivityById(request))
        ));

        return TaskWireMapper.toTaskResponse(this.taskQueryService.byId(TaskWireMapper.toTaskById(defined)));
    }

    @Operation(summary = "Returns the pending tasks, optionally those of a given activity")
    @GetMapping("/pending")
    public PendingTasksResponse pending(@RequestParam(required = false) String activityCode) {
        return TaskWireMapper.toPendingTasksResponse(
            this.taskQueryService.pending(TaskWireMapper.toPendingTasks(activityCode))
        );
    }

    @Operation(summary = "Returns a task by its code")
    @GetMapping("/{taskCode}")
    public TaskResponse task(@PathVariable String taskCode) {
        return TaskWireMapper.toTaskResponse(this.taskQueryService.task(TaskWireMapper.toTaskByCode(taskCode)));
    }

    @Operation(summary = "Registers a delivery for a task and returns the task")
    @PostMapping("/{taskCode}/delivery")
    public TaskResponse registerDelivery(@PathVariable String taskCode, @RequestBody RegisterDeliveryRequest request) {
        this.taskCommandService.registerDelivery(TaskWireMapper.toRegisterTaskDelivery(taskCode, request));

        return this.task(taskCode);
    }

    @Operation(summary = "Completes a task whose delivery was confirmed and returns it")
    @PostMapping("/{taskCode}/completion")
    public TaskResponse complete(@PathVariable String taskCode, @RequestBody ReviewedDeliveryRequest request) {
        this.taskCommandService.complete(TaskWireMapper.toCompleteTask(taskCode, request));

        return this.task(taskCode);
    }

    @Operation(summary = "Reopens a task whose delivery was rejected and returns it")
    @PostMapping("/{taskCode}/reopening")
    public TaskResponse reopen(@PathVariable String taskCode, @RequestBody ReviewedDeliveryRequest request) {
        this.taskCommandService.reopen(TaskWireMapper.toReopenTask(taskCode, request));

        return this.task(taskCode);
    }

    @Operation(summary = "Relinks a delivery to another task and returns the task it left")
    @PostMapping("/{taskCode}/delivery/relink")
    public TaskResponse relinkDelivery(@PathVariable String taskCode, @RequestBody RelinkDeliveryRequest request) {
        this.taskCommandService.relinkDelivery(TaskWireMapper.toRelinkTaskDelivery(taskCode, request));

        return this.task(request.fromTaskCode());
    }
}
