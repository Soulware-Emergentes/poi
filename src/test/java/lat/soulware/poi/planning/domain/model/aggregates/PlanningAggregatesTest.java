package lat.soulware.poi.planning.domain.model.aggregates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import lat.soulware.poi.planning.domain.model.entities.Activity;
import lat.soulware.poi.planning.domain.model.entities.Objective;
import lat.soulware.poi.planning.domain.model.events.TaskCompleted;
import lat.soulware.poi.planning.domain.model.events.TaskDefined;
import lat.soulware.poi.planning.domain.model.exceptions.ActivityNotFoundException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingBeneficiaryIdException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingDescriptionException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingNameException;
import lat.soulware.poi.planning.domain.model.exceptions.MissingTargetException;
import lat.soulware.poi.planning.domain.model.exceptions.ObjectiveNotFoundException;
import lat.soulware.poi.planning.domain.model.exceptions.PlanCodeTakenException;
import lat.soulware.poi.planning.domain.model.exceptions.TaskNotDeliveredException;
import lat.soulware.poi.planning.domain.model.exceptions.TaskNotPendingException;
import lat.soulware.poi.planning.domain.model.exceptions.TaskWithoutActivityException;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityCode;
import lat.soulware.poi.planning.domain.model.valueobjects.ActivityId;
import lat.soulware.poi.planning.domain.model.valueobjects.BeneficiaryId;
import lat.soulware.poi.planning.domain.model.valueobjects.Description;
import lat.soulware.poi.planning.domain.model.valueobjects.ExecutionDate;
import lat.soulware.poi.planning.domain.model.valueobjects.MeasurementUnit;
import lat.soulware.poi.planning.domain.model.valueobjects.Name;
import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveCode;
import lat.soulware.poi.planning.domain.model.valueobjects.ObjectiveId;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanCode;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanId;
import lat.soulware.poi.planning.domain.model.valueobjects.SubmissionId;
import lat.soulware.poi.planning.domain.model.valueobjects.Target;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskCode;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskStatus;

/**
 * Covers what each level of the plan refuses to exist without (a name and a description, the
 * level above it, and for an activity, what it measures and how much of it), how each level numbers
 * the one below it, a task's lifecycle and what it announces, and when an activity, an objective
 * and the plan are complete.
 */
class PlanningAggregatesTest {

    private static final SubmissionId SHEET_A = new SubmissionId(UUID.randomUUID());
    private static final SubmissionId SHEET_B = new SubmissionId(UUID.randomUUID());

    private static final Name NAMED = new Name("Seguimiento a beneficiarios");
    private static final Description DESCRIBED = new Description("Visitas de seguimiento a beneficiarios");

    @Test
    void aPlanNeedsAName() {
        assertThrows(MissingNameException.class, () -> Plan.define(newPlanId(), planCode(), null, DESCRIBED, Optional.empty()));
    }

    @Test
    void aPlanNeedsADescription() {
        assertThrows(MissingDescriptionException.class, () -> Plan.define(newPlanId(), planCode(), NAMED, null, Optional.empty()));
    }

    @Test
    void aPlanCodeIsCarriedByOnePlan() {
        Plan holder = Plan.define(newPlanId(), planCode(), NAMED, DESCRIBED, Optional.empty());

        assertThrows(
            PlanCodeTakenException.class,
            () -> Plan.define(newPlanId(), planCode(), NAMED, DESCRIBED, Optional.of(holder))
        );
    }

    @Test
    void aPlanNumbersItsObjectivesInTheOrderItDefinesThem() {
        Plan plan = newPlan();

        plan.defineObjective(newObjectiveId(), NAMED, DESCRIBED);
        plan.defineObjective(newObjectiveId(), NAMED, DESCRIBED);

        assertEquals(
            List.of("2025-2030-01", "2025-2030-02"),
            plan.getObjectives().stream().map(objective -> objective.getCode().value()).toList()
        );
    }

    @Test
    void anObjectiveNumbersItsActivitiesInTheOrderItDefinesThem() {
        Plan plan = newPlan();
        ObjectiveId objectiveId = newObjectiveId();
        plan.defineObjective(objectiveId, NAMED, DESCRIBED);

        plan.defineActivity(objectiveId, newActivityId(), NAMED, DESCRIBED, unit(), new Target(10));
        plan.defineActivity(objectiveId, newActivityId(), NAMED, DESCRIBED, unit(), new Target(10));

        assertEquals(
            List.of("2025-2030-01-01", "2025-2030-01-02"),
            plan.getObjectives().getFirst().getActivities().stream().map(activity -> activity.getCode().value()).toList()
        );
    }

    @Test
    void anActivityGoesUnderAnObjectiveOfThePlan() {
        Plan plan = newPlan();

        assertThrows(
            ObjectiveNotFoundException.class,
            () -> plan.defineActivity(newObjectiveId(), newActivityId(), NAMED, DESCRIBED, unit(), new Target(10))
        );
    }

    @Test
    void anActivityIsSetAgainstATarget() {
        Plan plan = newPlan();
        ObjectiveId objectiveId = newObjectiveId();
        plan.defineObjective(objectiveId, NAMED, DESCRIBED);

        assertThrows(
            MissingTargetException.class,
            () -> plan.defineActivity(objectiveId, newActivityId(), NAMED, DESCRIBED, unit(), null)
        );
    }

    @Test
    void aTaskIsPlannedUnderAnActivity() {
        assertThrows(
            TaskWithoutActivityException.class,
            () -> BeneficiaryFollowUpTask.define(newTaskId(), null, code(), Optional.empty(), DESCRIBED, beneficiary())
        );
    }

    @Test
    void theFirstTaskUnderAnActivityIsNumberedOne() {
        BeneficiaryFollowUpTask task = BeneficiaryFollowUpTask.define(
            newTaskId(),
            newActivityId(),
            code(),
            Optional.empty(),
            DESCRIBED,
            beneficiary()
        );

        assertEquals(new TaskCode(code(), 1), task.getCode());
    }

    @Test
    void aTaskIsNumberedRightAfterTheLastUnderItsActivity() {
        Task last = pendingTask();

        BeneficiaryFollowUpTask task = BeneficiaryFollowUpTask.define(
            newTaskId(),
            newActivityId(),
            code(),
            Optional.of(last),
            DESCRIBED,
            beneficiary()
        );

        assertEquals(new TaskCode(code(), 33), task.getCode());
    }

    @Test
    void aDefinedTaskIsPending() {
        assertEquals(TaskStatus.PENDING, pendingTask().getStatus());
    }

    @Test
    void registeringADeliveryRecordsTheSubmissionAndWhen() {
        BeneficiaryFollowUpTask task = pendingTask();
        ExecutionDate executedOn = new ExecutionDate(LocalDate.of(2026, 10, 1));

        task.registerDelivery(SHEET_A, executedOn);

        assertEquals(TaskStatus.DELIVERED, task.getStatus());
        assertEquals(SHEET_A, task.getSubmissionId());
        assertEquals(executedOn, task.getExecutionDate());
    }

    @Test
    void aTaskTakesOneDeliveryAtATime() {
        BeneficiaryFollowUpTask task = pendingTask();
        task.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));

        assertThrows(TaskNotPendingException.class, () -> task.registerDelivery(SHEET_B, new ExecutionDate(LocalDate.now())));
    }

    @Test
    void aFollowUpTaskIsPlannedForABeneficiary() {
        assertThrows(
            MissingBeneficiaryIdException.class,
            () -> BeneficiaryFollowUpTask.define(newTaskId(), newActivityId(), code(), Optional.empty(), DESCRIBED, null)
        );
    }

    @Test
    void confirmingTheDeliveryCompletesTheTask() {
        BeneficiaryFollowUpTask task = pendingTask();
        task.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));

        task.complete(SHEET_A);

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertThrows(TaskNotPendingException.class, () -> task.registerDelivery(SHEET_B, new ExecutionDate(LocalDate.now())));
    }

    @Test
    void rejectingTheDeliveryLeavesTheTaskPendingWithoutIt() {
        BeneficiaryFollowUpTask task = pendingTask();
        task.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));

        task.reopen(SHEET_A);

        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertNull(task.getSubmissionId());
        assertEquals(beneficiary(), task.getBeneficiaryId());
        assertNull(task.getExecutionDate());
    }

    @Test
    void onlyTheHeldDeliveryIsConfirmed() {
        BeneficiaryFollowUpTask task = pendingTask();
        assertThrows(TaskNotDeliveredException.class, () -> task.complete(SHEET_A));

        task.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));

        assertThrows(TaskNotDeliveredException.class, () -> task.complete(SHEET_B));
    }

    @Test
    void theSameDeliveryRegisteredAgainChangesNothing() {
        BeneficiaryFollowUpTask task = pendingTask();
        task.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));

        task.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));

        assertEquals(SHEET_A, task.getSubmissionId());
    }

    @Test
    void aConfirmationAnnouncedAgainChangesNothing() {
        BeneficiaryFollowUpTask task = pendingTask();
        task.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));
        task.complete(SHEET_A);

        task.complete(SHEET_A);

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void aRejectionAnnouncedAgainLeavesTheNextDeliveryAlone() {
        BeneficiaryFollowUpTask task = pendingTask();
        task.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));
        task.reopen(SHEET_A);
        task.registerDelivery(SHEET_B, new ExecutionDate(LocalDate.now()));

        task.reopen(SHEET_A);

        assertEquals(TaskStatus.DELIVERED, task.getStatus());
        assertEquals(SHEET_B, task.getSubmissionId());
    }

    @Test
    void aCompletedTaskIsNotRejected() {
        BeneficiaryFollowUpTask task = pendingTask();
        task.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));
        task.complete(SHEET_A);

        assertThrows(TaskNotDeliveredException.class, () -> task.reopen(SHEET_A));
    }

    @Test
    void relinkingOntoAPendingTaskMovesTheDelivery() {
        BeneficiaryFollowUpTask source = pendingTask();
        BeneficiaryFollowUpTask target = taskCoded(otherTaskCode());
        source.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));

        target.relinkDeliveryFrom(source, SHEET_A);

        assertEquals(TaskStatus.PENDING, source.getStatus());
        assertEquals(SHEET_A, target.getSubmissionId());
    }

    @Test
    void relinkingOntoADeliveredTaskSwapsTheDeliveries() {
        BeneficiaryFollowUpTask source = pendingTask();
        BeneficiaryFollowUpTask target = taskCoded(otherTaskCode());
        source.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));
        target.registerDelivery(SHEET_B, new ExecutionDate(LocalDate.now()));

        target.relinkDeliveryFrom(source, SHEET_A);

        assertEquals(SHEET_B, source.getSubmissionId());
        assertEquals(SHEET_A, target.getSubmissionId());
    }

    @Test
    void aRelinkSentAgainChangesNothing() {
        BeneficiaryFollowUpTask source = pendingTask();
        BeneficiaryFollowUpTask target = taskCoded(otherTaskCode());
        source.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));
        target.registerDelivery(SHEET_B, new ExecutionDate(LocalDate.now()));
        target.relinkDeliveryFrom(source, SHEET_A);

        target.relinkDeliveryFrom(source, SHEET_A);

        assertEquals(SHEET_B, source.getSubmissionId());
        assertEquals(SHEET_A, target.getSubmissionId());
    }

    @Test
    void aCompletedTaskTakesNoRelinkedDelivery() {
        BeneficiaryFollowUpTask source = pendingTask();
        BeneficiaryFollowUpTask target = taskCoded(otherTaskCode());
        source.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));
        target.registerDelivery(SHEET_B, new ExecutionDate(LocalDate.now()));
        target.complete(SHEET_B);

        assertThrows(TaskNotPendingException.class, () -> target.relinkDeliveryFrom(source, SHEET_A));
        assertEquals(SHEET_A, source.getSubmissionId());
    }

    @Test
    void onlyADeliveryAwaitingReviewIsRelinked() {
        BeneficiaryFollowUpTask source = pendingTask();
        BeneficiaryFollowUpTask target = taskCoded(otherTaskCode());

        assertThrows(TaskNotDeliveredException.class, () -> target.relinkDeliveryFrom(source, SHEET_A));
    }

    @Test
    void definingATaskAnnouncesItUnderItsActivity() {
        TaskId taskId = newTaskId();
        ActivityId activityId = newActivityId();

        BeneficiaryFollowUpTask task = BeneficiaryFollowUpTask.define(
            taskId,
            activityId,
            code(),
            Optional.empty(),
            DESCRIBED,
            beneficiary()
        );

        TaskDefined defined = assertInstanceOf(TaskDefined.class, task.pullDomainEvents().getFirst());
        assertEquals(taskId, defined.taskId());
        assertEquals(activityId, defined.activityId());
    }

    @Test
    void completingATaskAnnouncesItOnce() {
        BeneficiaryFollowUpTask task = pendingTask();
        task.registerDelivery(SHEET_A, new ExecutionDate(LocalDate.now()));

        task.complete(SHEET_A);
        task.complete(SHEET_A);

        List<?> announced = task.pullDomainEvents();
        assertEquals(1, announced.size());
        TaskCompleted completed = assertInstanceOf(TaskCompleted.class, announced.getFirst());
        assertEquals(task.getId(), completed.taskId());
        assertEquals(task.getActivityId(), completed.activityId());
    }

    @Test
    void nothingIsCompleteBeforeItHasWorkUnderIt() {
        Plan plan = newPlan();
        assertFalse(plan.isComplete());

        ObjectiveId objectiveId = newObjectiveId();
        plan.defineObjective(objectiveId, NAMED, DESCRIBED);
        assertFalse(onlyObjective(plan).isComplete());

        plan.defineActivity(objectiveId, newActivityId(), NAMED, DESCRIBED, unit(), new Target(10));
        assertFalse(onlyActivity(plan).isComplete());
        assertFalse(plan.isComplete());
    }

    @Test
    void completingTheLastOpenTaskCompletesTheActivityItsObjectiveAndThePlan() {
        Plan plan = newPlan();
        ActivityId activityId = planWithOneActivity(plan);
        TaskId first = newTaskId();
        TaskId second = newTaskId();
        plan.trackTask(activityId, first);
        plan.trackTask(activityId, second);

        plan.recordTaskCompletion(activityId, first);
        assertFalse(onlyActivity(plan).isComplete());

        plan.recordTaskCompletion(activityId, second);
        assertTrue(onlyActivity(plan).isComplete());
        assertTrue(onlyObjective(plan).isComplete());
        assertTrue(plan.isComplete());
    }

    @Test
    void aTaskDefinedAfterCompletionOpensEverythingAgain() {
        Plan plan = newPlan();
        ActivityId activityId = planWithOneActivity(plan);
        TaskId done = newTaskId();
        plan.trackTask(activityId, done);
        plan.recordTaskCompletion(activityId, done);

        plan.trackTask(activityId, newTaskId());

        assertFalse(onlyActivity(plan).isComplete());
        assertFalse(onlyObjective(plan).isComplete());
        assertFalse(plan.isComplete());
    }

    @Test
    void hearingOfTheSameTaskTwiceChangesNothing() {
        Plan plan = newPlan();
        ActivityId activityId = planWithOneActivity(plan);
        TaskId taskId = newTaskId();

        plan.trackTask(activityId, taskId);
        plan.trackTask(activityId, taskId);
        plan.recordTaskCompletion(activityId, taskId);
        plan.recordTaskCompletion(activityId, taskId);

        assertTrue(plan.isComplete());
    }

    @Test
    void anObjectiveWaitsForEveryActivity() {
        Plan plan = newPlan();
        ObjectiveId objectiveId = newObjectiveId();
        plan.defineObjective(objectiveId, NAMED, DESCRIBED);
        ActivityId done = newActivityId();
        plan.defineActivity(objectiveId, done, NAMED, DESCRIBED, unit(), new Target(10));
        plan.defineActivity(objectiveId, newActivityId(), NAMED, DESCRIBED, unit(), new Target(10));
        TaskId taskId = newTaskId();
        plan.trackTask(done, taskId);

        plan.recordTaskCompletion(done, taskId);

        assertFalse(onlyObjective(plan).isComplete());
        assertFalse(plan.isComplete());
    }

    @Test
    void aTaskIsTrackedUnderAnActivityOfThePlan() {
        Plan plan = newPlan();

        assertThrows(ActivityNotFoundException.class, () -> plan.trackTask(newActivityId(), newTaskId()));
    }

    /** Defines one objective with one activity under it, and returns the activity's identity. */
    private static ActivityId planWithOneActivity(Plan plan) {
        ObjectiveId objectiveId = newObjectiveId();
        ActivityId activityId = newActivityId();
        plan.defineObjective(objectiveId, NAMED, DESCRIBED);
        plan.defineActivity(objectiveId, activityId, NAMED, DESCRIBED, unit(), new Target(10));
        return activityId;
    }

    private static Objective onlyObjective(Plan plan) {
        return plan.getObjectives().getFirst();
    }

    private static Activity onlyActivity(Plan plan) {
        return onlyObjective(plan).getActivities().getFirst();
    }

    private static BeneficiaryFollowUpTask pendingTask() {
        return taskCoded(taskCode());
    }

    /** A pending task as stored, so it carries the code a test needs. */
    private static BeneficiaryFollowUpTask taskCoded(TaskCode code) {
        return BeneficiaryFollowUpTask.reconstitute(
            newTaskId(),
            newActivityId(),
            code,
            DESCRIBED,
            beneficiary(),
            TaskStatus.PENDING,
            null,
            null
        );
    }

    private static Plan newPlan() {
        return Plan.define(newPlanId(), planCode(), NAMED, DESCRIBED, Optional.empty());
    }

    private static PlanId newPlanId() {
        return new PlanId(UUID.randomUUID());
    }

    private static ObjectiveId newObjectiveId() {
        return new ObjectiveId(UUID.randomUUID());
    }

    private static ActivityId newActivityId() {
        return new ActivityId(UUID.randomUUID());
    }

    private static TaskId newTaskId() {
        return new TaskId(UUID.randomUUID());
    }

    private static PlanCode planCode() {
        return new PlanCode(2025, 2030);
    }

    private static ObjectiveCode objectiveCode() {
        return new ObjectiveCode(planCode(), 1);
    }

    private static ActivityCode code() {
        return new ActivityCode(objectiveCode(), 3);
    }

    private static TaskCode otherTaskCode() {
        return new TaskCode(code(), 2);
    }

    private static TaskCode taskCode() {
        return new TaskCode(code(), 32);
    }

    private static MeasurementUnit unit() {
        return new MeasurementUnit("beneficiarios con visita de seguimiento");
    }

    private static BeneficiaryId beneficiary() {
        return new BeneficiaryId("DNI:12345678");
    }
}
