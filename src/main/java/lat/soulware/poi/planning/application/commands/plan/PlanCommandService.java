package lat.soulware.poi.planning.application.commands.plan;

import org.springframework.stereotype.Service;

import lat.soulware.poi.planning.application.commands.plan.handlers.DefineActivityHandler;
import lat.soulware.poi.planning.application.commands.plan.handlers.DefineObjectiveHandler;
import lat.soulware.poi.planning.application.commands.plan.handlers.DefinePlanHandler;
import lat.soulware.poi.planning.application.commands.plan.handlers.RecordTaskCompletionHandler;
import lat.soulware.poi.planning.application.commands.plan.handlers.TrackTaskHandler;
import lat.soulware.poi.planning.application.commands.plan.intent.DefineActivity;
import lat.soulware.poi.planning.application.commands.plan.intent.DefineObjective;
import lat.soulware.poi.planning.application.commands.plan.intent.DefinePlan;
import lat.soulware.poi.planning.application.commands.plan.intent.RecordTaskCompletion;
import lat.soulware.poi.planning.application.commands.plan.intent.TrackTask;
import lat.soulware.poi.planning.domain.model.valueobjects.PlanId;
import lat.soulware.poi.shared.application.commands.CommandService;

/**
 * The write entry point for plans, for the objectives and activities inside them, and for the
 * tasks each activity still has open.
 */
@Service
public class PlanCommandService implements CommandService {

    private final DefinePlanHandler definePlanHandler;
    private final DefineObjectiveHandler defineObjectiveHandler;
    private final DefineActivityHandler defineActivityHandler;
    private final TrackTaskHandler trackTaskHandler;
    private final RecordTaskCompletionHandler recordTaskCompletionHandler;

    public PlanCommandService(
        DefinePlanHandler definePlanHandler,
        DefineObjectiveHandler defineObjectiveHandler,
        DefineActivityHandler defineActivityHandler,
        TrackTaskHandler trackTaskHandler,
        RecordTaskCompletionHandler recordTaskCompletionHandler
    ) {
        this.definePlanHandler = definePlanHandler;
        this.defineObjectiveHandler = defineObjectiveHandler;
        this.defineActivityHandler = defineActivityHandler;
        this.trackTaskHandler = trackTaskHandler;
        this.recordTaskCompletionHandler = recordTaskCompletionHandler;
    }

    public PlanId define(DefinePlan command) {
        return this.definePlanHandler.handle(command);
    }

    public void defineObjective(DefineObjective command) {
        this.defineObjectiveHandler.handle(command);
    }

    public void defineActivity(DefineActivity command) {
        this.defineActivityHandler.handle(command);
    }

    public void trackTask(TrackTask command) {
        this.trackTaskHandler.handle(command);
    }

    public void recordTaskCompletion(RecordTaskCompletion command) {
        this.recordTaskCompletionHandler.handle(command);
    }
}
