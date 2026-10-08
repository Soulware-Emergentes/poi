package lat.soulware.poi.planning.application.commands.task.handlers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.commands.task.intent.DefineBeneficiaryFollowUpTask;
import lat.soulware.poi.planning.domain.model.aggregates.BeneficiaryFollowUpTask;
import lat.soulware.poi.planning.domain.model.aggregates.Task;
import lat.soulware.poi.planning.domain.model.valueobjects.TaskId;
import lat.soulware.poi.planning.domain.repositories.TaskRepository;
import lat.soulware.poi.shared.application.commands.handlers.CommandHandler;

/**
 * Defines a follow-up task under an activity, numbered after the activity's last task. The
 * activity's code arrives with the command, read beforehand, so the plan holding it is not loaded.
 *
 * <p>Serializable, because the number comes from the tasks already under the activity. Two
 * definitions could otherwise both read the same last task and take the same next number; Postgres
 * fails one of the two at commit instead.
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class DefineBeneficiaryFollowUpTaskHandler implements CommandHandler {

    private final TaskRepository taskRepository;

    public DefineBeneficiaryFollowUpTaskHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskId handle(DefineBeneficiaryFollowUpTask command) {
        Optional<Task> lastTask = this.taskRepository.findLastUnder(command.activityId());
        BeneficiaryFollowUpTask task = BeneficiaryFollowUpTask.define(
            new TaskId(UUID.randomUUID()),
            command.activityId(),
            command.activityCode(),
            lastTask,
            command.description(),
            command.beneficiaryId()
        );

        return this.taskRepository.save(task).getId();
    }
}
