package lat.soulware.poi.planning.application.commands.plan.handlers;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.commands.plan.intent.RecordTaskCompletion;
import lat.soulware.poi.planning.domain.model.aggregates.Plan;
import lat.soulware.poi.planning.domain.model.exceptions.ActivityNotFoundException;
import lat.soulware.poi.planning.domain.repositories.PlanRepository;
import lat.soulware.poi.shared.application.commands.handlers.CommandHandler;

/** Stops counting a completed task as open in the plan holding its activity. */
@Service
@Transactional
public class RecordTaskCompletionHandler implements CommandHandler {

    private final PlanRepository planRepository;

    public RecordTaskCompletionHandler(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public void handle(RecordTaskCompletion command) {
        Plan plan = this.planRepository.findByActivity(command.activityId())
            .orElseThrow(() -> new ActivityNotFoundException(List.of(command.activityId())));
        plan.recordTaskCompletion(command.activityId(), command.taskId());
        this.planRepository.save(plan);
    }
}
