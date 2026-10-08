package lat.soulware.poi.planning.infrastructure.persistence.plan.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** The Spring Data delegate behind {@code JpaPlanRepositoryAdapter}. */
public interface PlanJpaRepository extends JpaRepository<PlanEntity, UUID> {

    Optional<PlanEntity> findByCode(String code);
}
