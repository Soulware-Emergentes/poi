package lat.soulware.poi.planning.infrastructure.persistence.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.poi.planning.application.queries.tasks.TaskQueryService;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TasksPage;
import lat.soulware.poi.planning.application.queries.tasks.results.TasksPageResult;
import lat.soulware.poi.planning.domain.model.exceptions.UnknownTaskStatusException;
import lat.soulware.poi.shared.domain.model.exceptions.InvalidPageException;
import lat.soulware.poi.shared.domain.model.exceptions.InvalidSortDirectionException;
import lat.soulware.poi.shared.domain.model.exceptions.UnsortableColumnException;
import lat.soulware.poi.support.PostgresIntegrationTest;

/**
 * Reads pages of tasks through the query service: filtered, sorted and bounded.
 *
 * <p>Each test starts from one plan with two activities and four tasks under them, one pending and
 * three delivered on different days.
 */
@SpringBootTest
@Transactional
class TasksPageIT extends PostgresIntegrationTest {

    @Autowired
    private TaskQueryService taskQueryService;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void storeFourTasks() {
        this.jdbc.update("DELETE FROM tasks");
        this.jdbc.update("DELETE FROM activities");
        this.jdbc.update("DELETE FROM objectives");
        this.jdbc.update("DELETE FROM plans");

        UUID plan = UUID.randomUUID();
        UUID objective = UUID.randomUUID();
        this.jdbc.update("""
            INSERT INTO plans (id, code, name, description)
            VALUES (?, '2025-2030', 'Plan 2025-2030', 'Plan operativo institucional 2025-2030')""", plan);
        this.jdbc.update("""
            INSERT INTO objectives (id, plan_id, code, name, description)
            VALUES (?, ?, '2025-2030-01', 'Monitoreo', 'Monitoreo de beneficiarios')""", objective, plan);
        UUID first = this.storeActivity(objective, "2025-2030-01-01");
        UUID second = this.storeActivity(objective, "2025-2030-01-02");

        this.storeTask(first, "2025-2030-01-01-0001", "PENDING", null);
        this.storeTask(first, "2025-2030-01-01-0002", "DELIVERED", LocalDate.of(2030, 5, 1));
        this.storeTask(first, "2025-2030-01-01-0003", "COMPLETED", LocalDate.of(2030, 6, 1));
        this.storeTask(second, "2025-2030-01-02-0001", "DELIVERED", LocalDate.of(2030, 4, 1));
    }

    @Test
    void theLatestExecutionComesFirstAndPendingTasksLast() {
        assertThat(this.codes(this.page(null, null, null, "executionDate", "desc", 0, 10))).containsExactly(
            "2025-2030-01-01-0003",
            "2025-2030-01-01-0002",
            "2025-2030-01-02-0001",
            "2025-2030-01-01-0001"
        );
        assertThat(this.codes(this.page(null, null, null, "executionDate", "asc", 0, 10))).containsExactly(
            "2025-2030-01-02-0001",
            "2025-2030-01-01-0002",
            "2025-2030-01-01-0003",
            "2025-2030-01-01-0001"
        );
    }

    @Test
    void codesSortInPlanOrder() {
        assertThat(this.codes(this.page(null, null, null, "code", "asc", 0, 10))).containsExactly(
            "2025-2030-01-01-0001",
            "2025-2030-01-01-0002",
            "2025-2030-01-01-0003",
            "2025-2030-01-02-0001"
        );
    }

    @Test
    void filtersNarrowTheSelection() {
        assertThat(this.codes(this.page("DELIVERED", null, null, "code", "asc", 0, 10)))
            .containsExactly("2025-2030-01-01-0002", "2025-2030-01-02-0001");
        assertThat(this.codes(this.page(null, "-0001", null, "code", "asc", 0, 10)))
            .containsExactly("2025-2030-01-01-0001", "2025-2030-01-02-0001");
        assertThat(this.codes(this.page(null, null, "2025-2030-01-02", "code", "asc", 0, 10)))
            .containsExactly("2025-2030-01-02-0001");
        assertThat(this.codes(this.page("DELIVERED", null, "2025-2030-01-01", "code", "asc", 0, 10)))
            .containsExactly("2025-2030-01-01-0002");
    }

    @Test
    void aPageHoldsItsShareAndCountsTheWholeSelection() {
        TasksPageResult page = this.page(null, null, null, "code", "asc", 1, 3);

        assertThat(this.codes(page)).containsExactly("2025-2030-01-02-0001");
        assertThat(page.totalElements()).isEqualTo(4);
        assertThat(page.totalPages()).isEqualTo(2);
        assertThat(page.actualPage()).isEqualTo(1);
        assertThat(page.pageSize()).isEqualTo(3);
    }

    @Test
    void whatNoPageCanBeIsRefused() {
        assertThatExceptionOfType(UnknownTaskStatusException.class)
            .isThrownBy(() -> this.page("SUBMITTED", null, null, "code", "asc", 0, 10));
        assertThatExceptionOfType(UnsortableColumnException.class)
            .isThrownBy(() -> this.page(null, null, null, "description", "asc", 0, 10));
        assertThatExceptionOfType(InvalidSortDirectionException.class)
            .isThrownBy(() -> this.page(null, null, null, "code", "up", 0, 10));
        assertThatExceptionOfType(InvalidPageException.class)
            .isThrownBy(() -> this.page(null, null, null, "code", "asc", -1, 10));
        assertThatExceptionOfType(InvalidPageException.class)
            .isThrownBy(() -> this.page(null, null, null, "code", "asc", 0, 101));
    }

    private TasksPageResult page(
        String status,
        String code,
        String activityCode,
        String sort,
        String direction,
        int page,
        int size
    ) {
        return this.taskQueryService.page(new TasksPage(status, code, activityCode, sort, direction, page, size));
    }

    private List<String> codes(TasksPageResult page) {
        return page.content().stream()
            .map(taskResult -> taskResult.code())
            .toList();
    }

    private UUID storeActivity(UUID objective, String code) {
        UUID activity = UUID.randomUUID();
        this.jdbc.update("""
            INSERT INTO activities (id, objective_id, code, name, description, measurement_unit, target)
            VALUES (?, ?, ?, 'Seguimiento a beneficiarios', 'Visitas de seguimiento a beneficiarios',
                'beneficiarios con visita de seguimiento', 2500)""",
            activity, objective, code
        );

        return activity;
    }

    /** A pending task has no execution date; any other carries one, with the submission behind it. */
    private void storeTask(UUID activity, String code, String status, LocalDate executionDate) {
        UUID submission = executionDate == null ? null : UUID.randomUUID();
        this.jdbc.update("""
            INSERT INTO tasks (id, activity_id, code, description, kind, status, submission_id, execution_date,
                beneficiary_id)
            VALUES (?, ?, ?, 'Visita de seguimiento', 'BENEFICIARY_FOLLOW_UP', ?, ?, ?, 'DNI:12345678')""",
            UUID.randomUUID(), activity, code, status, submission, executionDate
        );
    }
}
