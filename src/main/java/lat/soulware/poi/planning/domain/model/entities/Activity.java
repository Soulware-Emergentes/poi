package lat.soulware.poi.planning.domain.model.entities;

import lat.soulware.poi.planning.domain.model.exceptions.MissingActivityCodeException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingDescriptionException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingNameException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingMeasurementUnitException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingTargetException;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityCode;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.Name;
import lat.soulware.poi.planning.domain.model.valueobjects.MeasurementUnit;
import lat.soulware.poi.planning.domain.model.valueobjects.Target;
import lat.soulware.poi.shared.domain.model.entities.Entity;

/**
 * Work an objective is pursued through, measured in a unit and set against a target. Each task
 * completed under it delivers one unit, and a unit delivered twice counts once.
 */
public class Activity extends Entity<ActivityId> {
    private final ActivityId id;
    private final ActivityCode code;
    private final Name name;
    private final Description description;
    private final MeasurementUnit measurementUnit;
    private final Target target;

    private Activity(
        ActivityId id,
        ActivityCode code,
        Name name,
        Description description,
        MeasurementUnit measurementUnit,
        Target target
    ) {
        if (code == null) {
            throw new MissingActivityCodeException();
        }
        if (name == null) {
            throw new MissingNameException();
        }
        if (description == null) {
            throw new MissingDescriptionException();
        }
        if (measurementUnit == null) {
            throw new MissingMeasurementUnitException();
        }
        if (target == null) {
            throw new MissingTargetException();
        }
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.measurementUnit = measurementUnit;
        this.target = target;
    }

    /**
     * @param id              the activity's identity
     * @param code            the code its objective numbered it with
     * @param name            what the activity is called
     * @param description     what the activity is
     * @param measurementUnit what it counts toward its target
     * @param target          how many units it sets out to deliver
     * @return the defined activity
     */
    public static Activity define(
        ActivityId id,
        ActivityCode code,
        Name name,
        Description description,
        MeasurementUnit measurementUnit,
        Target target
    ) {
        return new Activity(id, code, name, description, measurementUnit, target);
    }

    /**
     * Rebuilds an activity already defined.
     *
     * @param id              the activity's identity
     * @param code            the code its objective numbered it with
     * @param name            what the activity is called
     * @param description     what the activity is
     * @param measurementUnit what it counts toward its target
     * @param target          how many units it sets out to deliver
     * @return the activity as stored
     */
    public static Activity reconstitute(
        ActivityId id,
        ActivityCode code,
        Name name,
        Description description,
        MeasurementUnit measurementUnit,
        Target target
    ) {
        return new Activity(id, code, name, description, measurementUnit, target);
    }

    @Override
    public ActivityId getId() {
        return this.id;
    }

    public ActivityCode getCode() {
        return this.code;
    }

    public Name getName() {
        return this.name;
    }

    public Description getDescription() {
        return this.description;
    }

    public MeasurementUnit getMeasurementUnit() {
        return this.measurementUnit;
    }

    public Target getTarget() {
        return this.target;
    }
}
