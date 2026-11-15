package lat.soulware.poi.planning.domain.model.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import lat.soulware.poi.planning.domain.model.exceptions.MissingObjectiveCodeException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingDescriptionException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingNameException;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityCode;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.MeasurementUnit;
import lat.soulware.poi.planning.domain.model.valueobjects.Name;
import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveCode;
import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveId;
import lat.soulware.poi.planning.domain.model.valueobjects.Target;
import lat.soulware.poi.shared.domain.model.entities.Entity;

/**
 * What a plan sets out to achieve, inside the plan that defines it. Its progress is its activities'
 * progress; it holds no target of its own. Its activities are numbered under it in the order they
 * are defined. It is complete once it has activities and every one of them is complete.
 */
public class Objective extends Entity<ObjectiveId> {
    private final ObjectiveId id;
    private final ObjectiveCode code;
    private final Name name;
    private final Description description;
    private final List<Activity> activities;

    private Objective(
        ObjectiveId id,
        ObjectiveCode code,
        Name name,
        Description description,
        List<Activity> activities
    ) {
        if (code == null) {
            throw new MissingObjectiveCodeException();
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
        this.activities = new ArrayList<>(activities);
    }

    /**
     * @param id          the objective's identity
     * @param code        the code its plan numbered it with
     * @param name        what the objective is called
     * @param description what the objective is
     * @return the defined objective, with no activities yet
     */
    public static Objective define(ObjectiveId id, ObjectiveCode code, Name name, Description description) {
        return new Objective(id, code, name, description, List.of());
    }

    /**
     * Rebuilds an objective already defined.
     *
     * @param id          the objective's identity
     * @param code        the code its plan numbered it with
     * @param name        what the objective is called
     * @param description what the objective is
     * @param activities  the activities defined under it
     * @return the objective as stored
     */
    public static Objective reconstitute(
        ObjectiveId id,
        ObjectiveCode code,
        Name name,
        Description description,
        List<Activity> activities
    ) {
        return new Objective(id, code, name, description, activities);
    }

    /**
     * Defines an activity under this objective, numbered right after the last one, or 01 when it is
     * the first.
     *
     * @param id              the activity's identity
     * @param name            what the activity is called
     * @param description     what the activity is
     * @param measurementUnit what it counts toward its target
     * @param target          how many units it sets out to deliver
     */
    public void defineActivity(
        ActivityId id,
        Name name,
        Description description,
        MeasurementUnit measurementUnit,
        Target target
    ) {
        ActivityCode code = this.activities.stream()
            .map(activity -> activity.getCode())
            .max(Comparator.comparingInt(activityCode -> activityCode.number()))
            .map(activityCode -> activityCode.next())
            .orElse(new ActivityCode(this.code, 1));

        this.activities.add(Activity.define(id, code, name, description, measurementUnit, target));
    }

    /**
     * @param activityId an activity's identity
     * @return the activity under this objective with that identity, empty when it holds none
     */
    public Optional<Activity> activity(ActivityId activityId) {
        return this.activities.stream()
            .filter(activity -> activity.getId().equals(activityId))
            .findFirst();
    }

    /** @return whether it has activities and every one of them is complete */
    public boolean isComplete() {
        return !this.activities.isEmpty() && this.activities.stream()
            .allMatch(activity -> activity.isComplete());
    }

    @Override
    public ObjectiveId getId() {
        return this.id;
    }

    public ObjectiveCode getCode() {
        return this.code;
    }

    public Name getName() {
        return this.name;
    }

    public Description getDescription() {
        return this.description;
    }

    public List<Activity> getActivities() {
        return List.copyOf(this.activities);
    }
}
