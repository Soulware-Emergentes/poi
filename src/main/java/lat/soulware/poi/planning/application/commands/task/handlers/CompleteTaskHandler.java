package lat.soulware.poi.planning.application.commands.task.handlers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.commands.task.intent.CompleteTask;
import lat.soulware.poi.planning.domain.model.aggregates.Task;
import lat.soulware.poi.planning.domain.model.exceptions.UnknownTaskCodeException;
import lat.soulware.poi.planning.domain.repositories.TaskRepository;
import lat.soulware.poi.shared.application.commands.handlers.CommandHandler;

/** Completes the task named by its code, once the delivery it holds has been confirmed by review. */
@Service
@Transactional
public class CompleteTaskHandler implements CommandHandler {

    private final TaskRepository taskRepository;

    public CompleteTaskHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void handle(CompleteTask command) {
        Task task = this.taskRepository.findByCode(command.taskCode())
            .orElseThrow(() -> new UnknownTaskCodeException(command.taskCode().value()));
        task.complete(command.submissionId());
        this.taskRepository.save(task);
    }
}
