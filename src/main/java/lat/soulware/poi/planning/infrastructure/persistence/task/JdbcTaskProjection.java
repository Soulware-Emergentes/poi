package lat.soulware.poi.planning.infrastructure.persistence.task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import lat.soulware.poi.planning.application.queries.tasks.criteria.AllTasks;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskByCode;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskById;
import lat.soulware.poi.planning.application.queries.tasks.projections.TaskProjection;
import lat.soulware.poi.planning.application.queries.tasks.results.TaskResult;

/** Reads one task by its code or its identity, or every task. */
@Component
public class JdbcTaskProjection implements TaskProjection {

    private static final String FIND_BY_CODE = """
        SELECT code, description, status, submission_id, beneficiary_id
        FROM tasks
        WHERE code = :code""";

    private static final String FIND_BY_ID = """
        SELECT code, description, status, submission_id, beneficiary_id
        FROM tasks
        WHERE id = :id""";

    private static final String FIND_ALL = """
        SELECT code, description, status, submission_id, beneficiary_id
        FROM tasks
        ORDER BY code""";

    private static final RowMapper<TaskResult> TASK = (row, rowNumber) -> new TaskResult(
        row.getString("code"),
        row.getString("description"),
        row.getString("status"),
        row.getObject("submission_id", UUID.class),
        row.getString("beneficiary_id")
    );

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcTaskProjection(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<TaskResult> findByCode(TaskByCode criteria) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("code", criteria.code());

        return this.jdbc.query(FIND_BY_CODE, parameters, TASK).stream()
            .findFirst();
    }

    @Override
    public Optional<TaskResult> findById(TaskById criteria) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("id", criteria.taskId());

        return this.jdbc.query(FIND_BY_ID, parameters, TASK).stream()
            .findFirst();
    }

    @Override
    public List<TaskResult> findAll(AllTasks criteria) {
        return this.jdbc.query(FIND_ALL, new MapSqlParameterSource(), TASK);
    }
}
