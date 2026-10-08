package lat.soulware.poi.planning.infrastructure.persistence.plan.jpa;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * One objective of a plan, as a row of {@code objectives}, with the rows of its activities. Its
 * {@code plan_id} is written from the plan's side.
 */
@Entity
@Table(name = "objectives")
public class ObjectiveRow {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "objective_id", nullable = false, updatable = false)
    private Set<ActivityRow> activities = new LinkedHashSet<>();

    /** For JPA. */
    protected ObjectiveRow() {
    }

    public ObjectiveRow(UUID id, String code, String name, String description, Set<ActivityRow> activities) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.activities = activities;
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

    public Set<ActivityRow> getActivities() {
        return this.activities;
    }
}
