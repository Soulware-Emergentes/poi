package lat.soulware.poi.planning.domain.model.aggregates;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import lat.soulware.poi.planning.domain.model.entities.Objective;
import lat.soulware.poi.planning.domain.model.exceptions.MissingPlanCodeException;
import lat.soulware.poi.planning.domain.model.exceptions.PlanCodeTakenException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingDescriptionException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingNameException;
import lat.soulware.poi.planning.domain.model.exceptions.ObjectiveNotFoundException;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.MeasurementUnit;
import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveCode;
import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveId;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanCode;
import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.Name;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanId;
import lat.soulware.poi.planning.domain.model.valueobjects.Target;
import lat.soulware.poi.shared.domain.model.aggregates.AggregateRoot;
import lat.soulware.poi.shared.domain.model.aggregates.PackagedAs;

/**
 * An institutional operational plan, holding the objectives it sets out to achieve and the
 * activities each is pursued through. The plan numbers its objectives in the order it defines them,
 * and each objective numbers its activities the same way, so a code is never chosen by hand. No two
 * plans carry the same code.
 *
 * <p>Tasks hang from its activities but are not part of it: there are thousands per activity, and
 * each changes on its own as deliveries arrive.
 */
@PackagedAs("plan")
public class Plan extends AggregateRoot<PlanId> {
    private final PlanId id;
    private final PlanCode code;
    private final Name name;
    private final Description description;
    private final List<Objective> objectives;

    private Plan(PlanId id, PlanCode code, Name name, Description description, List<Objective> objectives) {
        if (code == null) {
            throw new MissingPlanCodeException();
        }
        if (name == null) {
            throw new MissingNameException();
        }
        if (description == null) {
            throw new MissingDescriptionException();
        }
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.objectives = new ArrayList<>(objectives);
    }

    /**
     * @param id          the plan's identity
     * @param code        the plan's business identity
     * @param name        what the plan is called
     * @param description what the plan is
     * @param sameCode    the plan already carrying {@code code}, empty when there is none
     * @return the defined plan, with no objectives yet
     * @throws PlanCodeTakenException if another plan carries the code
     */
    public static Plan define(
        PlanId id,
        PlanCode code,
        Name name,
        Description description,
        Optional<Plan> sameCode
    ) {
        Plan plan = new Plan(id, code, name, description, List.of());
        if (sameCode.isPresent()) {
            throw new PlanCodeTakenException(code.value());
        }
        return plan;
    }

    /**
     * Rebuilds a plan already defined.
     *
     * @param id          the plan's identity
     * @param code        the plan's business identity
     * @param name        what the plan is called
     * @param description what the plan is
     * @param objectives  the objectives defined under it, with their activities
     * @return the plan as stored
     */
    public static Plan reconstitute(
        PlanId id,
        PlanCode code,
        Name name,
        Description description,
        List<Objective> objectives
    ) {
        return new Plan(id, code, name, description, objectives);
    }

    /**
     * Defines an objective under this plan, numbered right after the last one, or 01 when it is the
     * first.
     *
     * @param id          the objective's identity
     * @param name        what the objective is called
     * @param description what the objective is
     */
    public void defineObjective(ObjectiveId id, Name name, Description description) {
        ObjectiveCode code = this.objectives.stream()
            .map(objective -> objective.getCode())
            .max(Comparator.comparingInt(objectiveCode -> objectiveCode.number()))
            .map(objectiveCode -> objectiveCode.next())
            .orElse(new ObjectiveCode(this.code, 1));

        this.objectives.add(Objective.define(id, code, name, description));
    }

    /**
     * Defines an activity under one of this plan's objectives, numbered right after the last one
     * there.
     *
     * @param objectiveId     the objective it pursues
     * @param id              the activity's identity
     * @param name            what the activity is called
     * @param description     what the activity is
     * @param measurementUnit what it counts toward its target
     * @param target          how many units it sets out to deliver
     * @throws ObjectiveNotFoundException if the plan holds no such objective
     */
    public void defineActivity(
        ObjectiveId objectiveId,
        ActivityId id,
        Name name,
        Description description,
        MeasurementUnit measurementUnit,
        Target target
    ) {
        Objective objective = this.objectives.stream()
            .filter(candidate -> candidate.getId().equals(objectiveId))
            .findFirst()
            .orElseThrow(() -> new ObjectiveNotFoundException(List.of(objectiveId)));

        objective.defineActivity(id, name, description, measurementUnit, target);
    }

    @Override
    public PlanId getId() {
        return this.id;
    }

    public PlanCode getCode() {
        return this.code;
    }

    public Name getName() {
        return this.name;
    }

    public Description getDescription() {
        return this.description;
    }

    public List<Objective> getObjectives() {
        return List.copyOf(this.objectives);
    }
}
