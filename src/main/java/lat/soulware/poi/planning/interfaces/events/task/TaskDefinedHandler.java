package lat.soulware.poi.planning.interfaces.events.task;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import lat.soulware.poi.planning.application.commands.plan.PlanCommandService;
import lat.soulware.poi.planning.application.commands.plan.intent.TrackTask;
import lat.soulware.poi.planning.domain.model.events.TaskDefined;
import lat.soulware.poi.shared.interfaces.events.EventHandler;

/** Counts each newly defined task as open in the plan holding its activity. */
@Component
public class TaskDefinedHandler implements EventHandler {

    private final PlanCommandService planCommandService;

    public TaskDefinedHandler(PlanCommandService planCommandService) {
        this.planCommandService = planCommandService;
    }

    @ApplicationModuleListener
    public void on(TaskDefined event) {
        this.planCommandService.trackTask(new TrackTask(event.activityId(), event.taskId()));
    }
}
