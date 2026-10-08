package lat.soulware.poi.planning.application.commands.task.handlers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.commands.task.intent.ReopenTask;
import lat.soulware.poi.planning.domain.model.aggregates.Task;
import lat.soulware.poi.planning.domain.model.exceptions.UnknownTaskCodeException;
import lat.soulware.poi.planning.domain.repositories.TaskRepository;
import lat.soulware.poi.shared.application.commands.handlers.CommandHandler;

/** Sends the task named by its code back to pending, its delivery rejected on review. */
@Service
@Transactional
public class ReopenTaskHandler implements CommandHandler {

    private final TaskRepository taskRepository;

    public ReopenTaskHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void handle(ReopenTask command) {
        Task task = this.taskRepository.findByCode(command.taskCode())
            .orElseThrow(() -> new UnknownTaskCodeException(command.taskCode().value()));
        task.reopen(command.submissionId());
        this.taskRepository.save(task);
    }
}
