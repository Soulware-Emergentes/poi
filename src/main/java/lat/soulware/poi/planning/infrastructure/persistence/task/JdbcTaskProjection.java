package lat.soulware.poi.planning.infrastructure.persistence.task;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskByCode;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TaskById;
import lat.soulware.poi.planning.application.queries.tasks.criteria.TasksPage;
import lat.soulware.poi.planning.application.queries.tasks.projections.TaskProjection;
import lat.soulware.poi.planning.application.queries.tasks.results.TaskResult;
import lat.soulware.poi.planning.application.queries.tasks.results.TasksPageResult;

/** Reads one task by its code or its identity, or a page of tasks. */
@Component
public class JdbcTaskProjection implements TaskProjection {

    private static final String FIND_BY_CODE = """
        SELECT code, description, status, submission_id, execution_date, beneficiary_id
        FROM tasks
        WHERE code = :code""";

    private static final String FIND_BY_ID = """
        SELECT code, description, status, submission_id, execution_date, beneficiary_id
        FROM tasks
        WHERE id = :id""";

    private static final String MATCHING = """
        FROM tasks t
        JOIN activities a ON a.id = t.activity_id
        WHERE (CAST(:status AS VARCHAR) IS NULL OR t.status = :status)
            AND (CAST(:code AS VARCHAR) IS NULL OR lower(t.code) LIKE '%' || lower(:code) || '%')
            AND (CAST(:activityCode AS VARCHAR) IS NULL OR a.code = :activityCode)""";

    private static final String COUNT_PAGE = "SELECT count(*) " + MATCHING;

    private static final String FIND_PAGE = """
        SELECT t.code, t.description, t.status, t.submission_id, t.execution_date, t.beneficiary_id
        %s
        ORDER BY %s, t.id
        LIMIT :size OFFSET :offset""";

    /**
     * What each sortable column orders by, with {@code %1$s} standing for the direction. Codes are
     * fixed width, so their text order is the plan's order. Pending tasks have no execution date and
     * come last either way.
     */
    private static final Map<String, String> ORDER_BY = Map.of(
        "code", "t.code %1$s",
        "status", "t.status %1$s",
        "executionDate", "t.execution_date %1$s NULLS LAST"
    );

    private static final RowMapper<TaskResult> TASK = (row, rowNumber) -> new TaskResult(
        row.getString("code"),
        row.getString("description"),
        row.getString("status"),
        row.getObject("submission_id", UUID.class),
        row.getObject("execution_date", LocalDate.class),
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
    public TasksPageResult findPage(TasksPage criteria) {
        boolean isDescending = "desc".equalsIgnoreCase(criteria.direction());
        String direction = isDescending ? "DESC" : "ASC";
        String orderBy = ORDER_BY.get(criteria.sort()).formatted(direction);
        MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("status", criteria.status())
            .addValue("code", criteria.code())
            .addValue("activityCode", criteria.activityCode())
            .addValue("size", criteria.size())
            .addValue("offset", (long) criteria.page() * criteria.size());

        long totalElements = this.jdbc.queryForObject(COUNT_PAGE, parameters, Long.class);
        List<TaskResult> content = this.jdbc.query(FIND_PAGE.formatted(MATCHING, orderBy), parameters, TASK);
        int totalPages = (int) Math.ceilDiv(totalElements, criteria.size());

        return new TasksPageResult(content, totalPages, totalElements, criteria.page(), criteria.size());
    }
}
