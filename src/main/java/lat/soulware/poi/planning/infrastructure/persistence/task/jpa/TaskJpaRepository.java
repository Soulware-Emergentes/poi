package lat.soulware.poi.planning.infrastructure.persistence.task.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** The Spring Data delegate behind {@code JpaTaskRepositoryAdapter}. */
public interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {

    Optional<TaskEntity> findByCode(String code);

    /** Codes are zero-padded, so the highest one sorts last. */
    Optional<TaskEntity> findFirstByActivityIdOrderByCodeDesc(UUID activityId);
}
