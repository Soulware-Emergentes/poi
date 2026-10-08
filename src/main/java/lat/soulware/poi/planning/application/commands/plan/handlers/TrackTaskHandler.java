package lat.soulware.poi.planning.application.commands.plan.handlers;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.commands.plan.intent.TrackTask;
import lat.soulware.poi.planning.domain.model.aggregates.Plan;
import lat.soulware.poi.planning.domain.model.exceptions.ActivityNotFoundException;
import lat.soulware.poi.planning.domain.repositories.PlanRepository;
import lat.soulware.poi.shared.application.commands.handlers.CommandHandler;

/** Counts a newly defined task as open in the plan holding its activity. */
@Service
@Transactional
public class TrackTaskHandler implements CommandHandler {

    private final PlanRepository planRepository;

    public TrackTaskHandler(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public void handle(TrackTask command) {
        Plan plan = this.planRepository.findByActivity(command.activityId())
            .orElseThrow(() -> new ActivityNotFoundException(List.of(command.activityId())));
        plan.trackTask(command.activityId(), command.taskId());
        this.planRepository.save(plan);
    }
}
