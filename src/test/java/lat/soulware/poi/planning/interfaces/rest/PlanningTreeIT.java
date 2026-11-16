package lat.soulware.poi.planning.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.jayway.jsonpath.JsonPath;

import lat.soulware.poi.support.PostgresIntegrationTest;
import lat.soulware.poi.support.TestTokens;

/**
 * Builds the operational plan over HTTP the way a person does from the frontend: a plan, objectives
 * under it, an activity under an objective, and tasks under the activity, each numbered by the level
 * above and answered with what was created, then reads the plan back whole and the tasks as a list.
 *
 * <p>Each test starts from empty tables.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PlanningTreeIT extends PostgresIntegrationTest {

    private static final String PERSON = "Bearer " + TestTokens.person(UUID.randomUUID());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void startFromAnEmptyPlan() {
        this.jdbc.update("DELETE FROM tasks");
        this.jdbc.update("DELETE FROM activities");
        this.jdbc.update("DELETE FROM objectives");
        this.jdbc.update("DELETE FROM plans");
    }

    @Test
    void eachDefinitionAnswersWithWhatItCreated() throws Exception {
        this.postPlan("2025-2030")
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.code").value("2025-2030"))
            .andExpect(jsonPath("$.objectives").isEmpty());
        String planId = this.read("$.plans[0].id");

        String objectiveId = this.idOf(this.defineObjective(planId)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value("2025-2030-01"))
            .andExpect(jsonPath("$.name").value("Monitoreo"))
            .andExpect(jsonPath("$.activities").isEmpty())
        );
        String activityId = this.idOf(this.defineActivity(planId, objectiveId)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value("2025-2030-01-01"))
            .andExpect(jsonPath("$.target").value(2500))
        );
        this.defineTask(activityId)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value("2025-2030-01-01-0001"))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.beneficiaryId").value("DNI:12345678"));
    }

    @Test
    void eachLevelIsNumberedByTheOneAbove() throws Exception {
        String planId = this.definePlan("2025-2030");
        String objectiveId = this.idOf(this.defineObjective(planId));
        this.defineObjective(planId);
        String activityId = this.idOf(this.defineActivity(planId, objectiveId));
        this.defineTask(activityId);
        this.defineTask(activityId);

        this.list("/api/v1/plans")
            .andExpect(jsonPath("$.plans[0].code").value("2025-2030"))
            .andExpect(jsonPath("$.plans[0].objectives[0].code").value("2025-2030-01"))
            .andExpect(jsonPath("$.plans[0].objectives[1].code").value("2025-2030-02"))
            .andExpect(jsonPath("$.plans[0].objectives[0].activities[0].code").value("2025-2030-01-01"))
            .andExpect(jsonPath("$.plans[0].objectives[0].activities[0].target").value(2500));
        this.list("/api/v1/tasks?sort=code&direction=asc")
            .andExpect(jsonPath("$.content[0].code").value("2025-2030-01-01-0001"))
            .andExpect(jsonPath("$.content[1].code").value("2025-2030-01-01-0002"))
            .andExpect(jsonPath("$.content[1].status").value("PENDING"))
            .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void aLevelUnderAParentThatDoesNotExistIsRefused() throws Exception {
        String planId = this.definePlan("2025-2030");

        this.defineObjective(UUID.randomUUID().toString()).andExpect(status().isNotFound());
        this.defineActivity(planId, UUID.randomUUID().toString()).andExpect(status().isNotFound());
        this.defineTask(UUID.randomUUID().toString()).andExpect(status().isNotFound());
    }

    @Test
    void aPlanCodeAlreadyTakenIsRefused() throws Exception {
        this.definePlan("2025-2030");

        this.postPlan("2025-2030")
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void aServiceCannotDefineTasks() throws Exception {
        String service = "Bearer " + TestTokens.service("tasks.read", "tasks.write");

        this.mockMvc.perform(post("/api/v1/tasks")
                .header("Authorization", service)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
            )
            .andExpect(status().isForbidden());
    }

    /** @return the new plan's id */
    private String definePlan(String code) throws Exception {
        return this.idOf(this.postPlan(code).andExpect(status().isCreated()));
    }

    /** Reads the id off what a definition answered with. */
    private String idOf(ResultActions defined) throws Exception {
        String body = defined.andReturn()
            .getResponse()
            .getContentAsString();

        return JsonPath.read(body, "$.id");
    }

    private ResultActions postPlan(String code) throws Exception {
        return this.define("/api/v1/plans", """
            {"code": "%s", "name": "Plan %s", "description": "Plan operativo institucional %s"}""".formatted(code, code, code)
        );
    }

    private ResultActions defineObjective(String planId) throws Exception {
        return this.define("/api/v1/plans/%s/objectives".formatted(planId), """
            {"name": "Monitoreo", "description": "Monitoreo de beneficiarios"}"""
        );
    }

    private ResultActions defineActivity(String planId, String objectiveId) throws Exception {
        return this.define("/api/v1/plans/%s/objectives/%s/activities".formatted(planId, objectiveId), """
            {"name": "Seguimiento a beneficiarios", "description": "Visitas de seguimiento a beneficiarios",
             "measurementUnit": "beneficiarios con visita de seguimiento", "target": 2500}"""
        );
    }

    private ResultActions defineTask(String activityId) throws Exception {
        return this.define("/api/v1/tasks", """
            {"activityId": "%s", "description": "Visita de seguimiento", "beneficiaryId": "DNI:12345678"}""".formatted(activityId)
        );
    }

    private ResultActions define(String path, String body) throws Exception {
        return this.mockMvc.perform(post(path)
            .header("Authorization", PERSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
        );
    }

    private ResultActions list(String path) throws Exception {
        return this.mockMvc.perform(get(path).header("Authorization", PERSON))
            .andExpect(status().isOk());
    }

    /** Reads one value off the plans list. */
    private String read(String path) throws Exception {
        String plans = this.list("/api/v1/plans")
            .andReturn()
            .getResponse()
            .getContentAsString();

        return JsonPath.read(plans, path);
    }
}
