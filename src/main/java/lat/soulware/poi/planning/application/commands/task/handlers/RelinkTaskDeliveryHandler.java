package lat.soulware.poi.planning.application.commands.task.handlers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.commands.task.intent.RelinkTaskDelivery;
import lat.soulware.poi.planning.domain.model.aggregates.Task;
import lat.soulware.poi.planning.domain.model.exceptions.UnknownTaskCodeException;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskCode;
import lat.soulware.poi.planning.domain.repositories.TaskRepository;
import lat.soulware.poi.shared.application.commands.handlers.CommandHandler;

/** Relinks a delivery between two tasks, saving both together. */
@Service
@Transactional
public class RelinkTaskDeliveryHandler implements CommandHandler {

    private final TaskRepository taskRepository;

    public RelinkTaskDeliveryHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void handle(RelinkTaskDelivery command) {
        Task target = this.find(command.taskCode());
        Task source = this.find(command.fromTaskCode());

        target.relinkDeliveryFrom(source, command.submissionId());
        this.taskRepository.save(source);
        this.taskRepository.save(target);
    }

    private Task find(TaskCode code) {
        return this.taskRepository.findByCode(code)
            .orElseThrow(() -> new UnknownTaskCodeException(code.value()));
    }
}
