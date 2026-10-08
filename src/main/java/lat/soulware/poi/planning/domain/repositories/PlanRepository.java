package lat.soulware.poi.planning.domain.repositories;

import java.util.Optional;

import lat.soulware.poi.planning.domain.model.aggregates.Plan;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanCode;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanId;
import lat.soulware.poi.shared.domain.repositories.DomainRepository;

/** Stores {@link Plan} aggregates. */
public interface PlanRepository extends DomainRepository<Plan, PlanId> {

    /**
     * @param code the plan's business identity
     * @return the plan carrying that code, empty when none does
     */
    Optional<Plan> findByCode(PlanCode code);
}
