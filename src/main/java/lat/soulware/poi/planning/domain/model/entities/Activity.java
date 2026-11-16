package lat.soulware.poi.planning.domain.model.entities;

import java.util.LinkedHashSet;
import java.util.Set;

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
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.shared.domain.model.entities.Entity;

/**
 * Work an objective is pursued through, measured in a unit and set against a target. Each task
 * completed under it delivers one unit, and a unit delivered twice counts once.
 *
 * <p>It is complete once it has tasks and every one of them is completed. It keeps the tasks still
 * open, so hearing of the same task twice changes nothing, and a task defined after it completed
 * opens it again.
 */
public class Activity extends Entity<ActivityId> {
    private final ActivityId id;
    private final ActivityCode code;
    private final Name name;
    private final Description description;
    private final MeasurementUnit measurementUnit;
    private final Target target;
    private final Set<TaskId> openTasks;
    private boolean hasTasks;

    private Activity(
        ActivityId id,
        ActivityCode code,
        Name name,
        Description description,
        MeasurementUnit measurementUnit,
        Target target,
        Set<TaskId> openTasks,
        boolean hasTasks
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
        this.openTasks = new LinkedHashSet<>(openTasks);
        this.hasTasks = hasTasks;
    }

    /**
     * @param id              the activity's identity
     * @param code            the code its objective numbered it with
     * @param name            what the activity is called
     * @param description     what the activity is
     * @param measurementUnit what it counts toward its target
     * @param target          how many units it sets out to deliver
     * @return the defined activity, with no tasks yet
     */
    public static Activity define(
        ActivityId id,
        ActivityCode code,
        Name name,
        Description description,
        MeasurementUnit measurementUnit,
        Target target
    ) {
        return new Activity(id, code, name, description, measurementUnit, target, Set.of(), false);
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
     * @param openTasks       the tasks defined under it and not yet completed
     * @param hasTasks        whether any task was ever defined under it
     * @return the activity as stored
     */
    public static Activity reconstitute(
        ActivityId id,
        ActivityCode code,
        Name name,
        Description description,
        MeasurementUnit measurementUnit,
        Target target,
        Set<TaskId> openTasks,
        boolean hasTasks
    ) {
        return new Activity(id, code, name, description, measurementUnit, target, openTasks, hasTasks);
    }

    /**
     * Counts a task defined under this activity as open, which leaves the activity incomplete until
     * the task is completed.
     *
     * @param taskId the defined task
     */
    public void trackTask(TaskId taskId) {
        this.openTasks.add(taskId);
        this.hasTasks = true;
    }

    /**
     * Stops counting a completed task as open. A task no longer open is left as it is.
     *
     * @param taskId the completed task
     */
    public void recordTaskCompletion(TaskId taskId) {
        this.openTasks.remove(taskId);
    }

    /** @return whether tasks were defined under it and every one of them is completed */
    public boolean isComplete() {
        return this.hasTasks && this.openTasks.isEmpty();
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

    public Set<TaskId> getOpenTasks() {
        return Set.copyOf(this.openTasks);
    }

    public boolean hasTasks() {
        return this.hasTasks;
    }
}
