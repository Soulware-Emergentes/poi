package lat.soulware.poi.planning.interfaces.events.task;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import lat.soulware.poi.planning.application.commands.plan.PlanCommandService;
import lat.soulware.poi.planning.application.commands.plan.intent.RecordTaskCompletion;
import lat.soulware.poi.planning.domain.model.events.TaskCompleted;
import lat.soulware.poi.shared.interfaces.events.EventHandler;

/** Stops counting each completed task as open in the plan holding its activity. */
@Component
public class TaskCompletedHandler implements EventHandler {

    private final PlanCommandService planCommandService;

    public TaskCompletedHandler(PlanCommandService planCommandService) {
        this.planCommandService = planCommandService;
    }

    @ApplicationModuleListener
    public void on(TaskCompleted event) {
        this.planCommandService.recordTaskCompletion(new RecordTaskCompletion(event.activityId(), event.taskId()));
    }
}
