package lat.soulware.poi.planning.infrastructure.persistence.plan.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

/** The Spring Data delegate behind {@code JpaPlanRepositoryAdapter}. */
public interface PlanJpaRepository extends JpaRepository<PlanEntity, UUID> {

    Optional<PlanEntity> findByCode(String code);

    /** The plan holding the activity, its row locked until the transaction ends. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PlanEntity p JOIN p.objectives o JOIN o.activities a WHERE a.id = :activityId")
    Optional<PlanEntity> findByActivityId(@Param("activityId") UUID activityId);
}
