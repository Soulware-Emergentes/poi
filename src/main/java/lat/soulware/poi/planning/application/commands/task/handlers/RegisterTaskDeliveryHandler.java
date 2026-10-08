package lat.soulware.poi.planning.application.commands.task.handlers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.commands.task.intent.RegisterTaskDelivery;
import lat.soulware.poi.planning.domain.model.aggregates.Task;
import lat.soulware.poi.planning.domain.model.exceptions.UnknownTaskCodeException;
import lat.soulware.poi.planning.domain.repositories.TaskRepository;
import lat.soulware.poi.shared.application.commands.handlers.CommandHandler;

/** Registers a delivery for the task named by its code, refusing one the task cannot take. */
@Service
@Transactional
public class RegisterTaskDeliveryHandler implements CommandHandler {

    private final TaskRepository taskRepository;

    public RegisterTaskDeliveryHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void handle(RegisterTaskDelivery command) {
        Task task = this.taskRepository.findByCode(command.taskCode())
            .orElseThrow(() -> new UnknownTaskCodeException(command.taskCode().value()));
        task.registerDelivery(command.submissionId(), command.executionDate());
        this.taskRepository.save(task);
    }
}
