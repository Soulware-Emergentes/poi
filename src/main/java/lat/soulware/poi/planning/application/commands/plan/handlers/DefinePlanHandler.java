package lat.soulware.poi.planning.application.commands.plan.handlers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.commands.plan.intent.DefinePlan;
import lat.soulware.poi.planning.domain.model.aggregates.Plan;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanId;
import lat.soulware.poi.planning.domain.repositories.PlanRepository;
import lat.soulware.poi.shared.application.commands.handlers.CommandHandler;

/** Defines a plan under a code no other plan carries. */
@Service
@Transactional
public class DefinePlanHandler implements CommandHandler {

    private final PlanRepository planRepository;

    public DefinePlanHandler(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public PlanId handle(DefinePlan command) {
        Optional<Plan> sameCode = this.planRepository.findByCode(command.code());
        Plan plan = Plan.define(
            new PlanId(UUID.randomUUID()),
            command.code(),
            command.name(),
            command.description(),
            sameCode
        );

        return this.planRepository.save(plan).getId();
    }
}
