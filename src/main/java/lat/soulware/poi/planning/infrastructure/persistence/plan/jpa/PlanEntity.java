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

/** The row storing a plan, with the rows of its objectives and, under those, their activities. */
@Entity
@Table(name = "plans")
public class PlanEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false, updatable = false)
    private Set<ObjectiveRow> objectives = new LinkedHashSet<>();

    /** For JPA. */
    protected PlanEntity() {
    }

    public PlanEntity(
        UUID id,
        String code,
        String name,
        String description,
        Set<ObjectiveRow> objectives
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.objectives = objectives;
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

    public Set<ObjectiveRow> getObjectives() {
        return this.objectives;
    }
}
