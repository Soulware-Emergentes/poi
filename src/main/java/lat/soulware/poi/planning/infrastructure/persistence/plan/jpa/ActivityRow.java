package lat.soulware.poi.planning.infrastructure.persistence.plan.jpa;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * One activity of an objective, as a row of {@code activities}. Its {@code objective_id} is
 * written from the objective's side.
 */
@Entity
@Table(name = "activities")
public class ActivityRow {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "measurement_unit", nullable = false)
    private String measurementUnit;

    @Column(nullable = false)
    private long target;

    /** For JPA. */
    protected ActivityRow() {
    }

    public ActivityRow(
        UUID id,
        String code,
        String name,
        String description,
        String measurementUnit,
        long target
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.measurementUnit = measurementUnit;
        this.target = target;
    }

    public UUID getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getMeasurementUnit() {
        return this.measurementUnit;
    }

    public long getTarget() {
        return this.target;
    }
}
