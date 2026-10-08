package lat.soulware.poi.planning.application.commands.task;

import org.springframework.stereotype.Service;

import lat.soulware.poi.planning.application.commands.task.handlers.CompleteTaskHandler;
import lat.soulware.poi.planning.application.commands.task.handlers.DefineBeneficiaryFollowUpTaskHandler;
import lat.soulware.poi.planning.application.commands.task.handlers.RegisterTaskDeliveryHandler;
import lat.soulware.poi.planning.application.commands.task.handlers.ReopenTaskHandler;
import lat.soulware.poi.planning.application.commands.task.handlers.RelinkTaskDeliveryHandler;
import lat.soulware.poi.planning.application.commands.task.intent.CompleteTask;
import lat.soulware.poi.planning.application.commands.task.intent.DefineBeneficiaryFollowUpTask;
import lat.soulware.poi.planning.application.commands.task.intent.RegisterTaskDelivery;
import lat.soulware.poi.planning.application.commands.task.intent.ReopenTask;
import lat.soulware.poi.planning.application.commands.task.intent.RelinkTaskDelivery;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.shared.application.commands.CommandService;

/** The write entry point for tasks. */
@Service
public class TaskCommandService implements CommandService {

    private final DefineBeneficiaryFollowUpTaskHandler defineBeneficiaryFollowUpTaskHandler;
    private final RegisterTaskDeliveryHandler registerTaskDeliveryHandler;
    private final CompleteTaskHandler completeTaskHandler;
    private final ReopenTaskHandler reopenTaskHandler;
    private final RelinkTaskDeliveryHandler relinkTaskDeliveryHandler;

    public TaskCommandService(
        DefineBeneficiaryFollowUpTaskHandler defineBeneficiaryFollowUpTaskHandler,
        RegisterTaskDeliveryHandler registerTaskDeliveryHandler,
        CompleteTaskHandler completeTaskHandler,
        ReopenTaskHandler reopenTaskHandler,
        RelinkTaskDeliveryHandler relinkTaskDeliveryHandler
    ) {
        this.defineBeneficiaryFollowUpTaskHandler = defineBeneficiaryFollowUpTaskHandler;
        this.registerTaskDeliveryHandler = registerTaskDeliveryHandler;
        this.completeTaskHandler = completeTaskHandler;
        this.reopenTaskHandler = reopenTaskHandler;
        this.relinkTaskDeliveryHandler = relinkTaskDeliveryHandler;
    }

    public TaskId defineBeneficiaryFollowUp(DefineBeneficiaryFollowUpTask command) {
        return this.defineBeneficiaryFollowUpTaskHandler.handle(command);
    }

    public void registerDelivery(RegisterTaskDelivery command) {
        this.registerTaskDeliveryHandler.handle(command);
    }

    public void complete(CompleteTask command) {
        this.completeTaskHandler.handle(command);
    }

    public void reopen(ReopenTask command) {
        this.reopenTaskHandler.handle(command);
    }

    public void relinkDelivery(RelinkTaskDelivery command) {
        this.relinkTaskDeliveryHandler.handle(command);
    }
}
