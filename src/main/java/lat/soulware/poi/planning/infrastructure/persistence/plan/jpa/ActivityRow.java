package lat.soulware.poi.planning.infrastructure.persistence.plan.jpa;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

/**
 * One activity of an objective, as a row of {@code activities}, with the tasks it still has open as
 * rows of {@code activity_open_tasks}. Its {@code objective_id} is written from the objective's side.
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

    @Column(name = "has_tasks", nullable = false)
    private boolean hasTasks;

    @Column(nullable = false)
    private boolean completed;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "activity_open_tasks", joinColumns = @JoinColumn(name = "activity_id"))
    @Column(name = "task_id", nullable = false)
    private Set<UUID> openTasks = new LinkedHashSet<>();

    /** For JPA. */
    protected ActivityRow() {
    }

    public ActivityRow(
        UUID id,
        String code,
        String name,
        String description,
        String measurementUnit,
        long target,
        boolean hasTasks,
        boolean completed,
        Set<UUID> openTasks
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.measurementUnit = measurementUnit;
        this.target = target;
        this.hasTasks = hasTasks;
        this.completed = completed;
        this.openTasks = openTasks;
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

    public boolean hasTasks() {
        return this.hasTasks;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public Set<UUID> getOpenTasks() {
        return this.openTasks;
    }
}
