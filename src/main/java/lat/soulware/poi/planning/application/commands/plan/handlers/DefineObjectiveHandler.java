package lat.soulware.poi.planning.application.commands.plan.handlers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.commands.plan.intent.DefineObjective;
import lat.soulware.poi.planning.domain.model.aggregates.Plan;
import lat.soulware.poi.planning.domain.repositories.PlanRepository;
import lat.soulware.poi.shared.application.commands.handlers.CommandHandler;

/**
 * Defines an objective under a plan, numbered by the plan.
 *
 * <p>Serializable, because the number comes from what the plan already holds. Two definitions under
 * the same parent could otherwise both read the same last number and take the same next one;
 * Postgres fails one of the two at commit instead.
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class DefineObjectiveHandler implements CommandHandler {

    private final PlanRepository planRepository;

    public DefineObjectiveHandler(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public void handle(DefineObjective command) {
        Plan plan = this.planRepository.getById(command.planId());
        plan.defineObjective(command.objectiveId(), command.name(), command.description());
        this.planRepository.save(plan);
    }
}
