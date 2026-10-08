package lat.soulware.poi.planning.infrastructure.persistence.task;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import lat.soulware.poi.planning.application.queries.tasks.criteria.PendingTasks;
import lat.soulware.poi.planning.application.queries.tasks.projections.PendingTaskProjection;
import lat.soulware.poi.planning.application.queries.tasks.results.PendingTaskResult;

/** Reads pending tasks with their activity in one query. */
@Component
public class JdbcPendingTaskProjection implements PendingTaskProjection {

    private static final String FIND_PENDING = """
        SELECT t.code, t.description, a.code AS activity_code, a.description AS activity_description,
            a.measurement_unit
        FROM tasks t
        JOIN activities a ON a.id = t.activity_id
        WHERE t.status = 'PENDING'
            AND (CAST(:activityCode AS VARCHAR) IS NULL OR a.code = :activityCode)
        ORDER BY t.code""";

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcPendingTaskProjection(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<PendingTaskResult> findPending(PendingTasks criteria) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("activityCode", criteria.activityCode());

        return this.jdbc.query(FIND_PENDING, parameters, (row, rowNumber) -> new PendingTaskResult(
            row.getString("code"),
            row.getString("description"),
            row.getString("activity_code"),
            row.getString("activity_description"),
            row.getString("measurement_unit")
        ));
    }
}
