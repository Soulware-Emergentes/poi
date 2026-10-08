package lat.soulware.poi.planning.interfaces.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.jayway.jsonpath.JsonPath;

import lat.soulware.poi.support.PostgresIntegrationTest;
import lat.soulware.poi.support.TestTokens;

/**
 * Completes tasks over HTTP the way sgt does and watches the plan follow: the activity, its
 * objective and the plan complete once every task under them is completed, and a task defined
 * afterwards opens them again. The plan hears of tasks through events, so each check waits for
 * the listener.
 *
 * <p>Each test starts from empty tables.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PlanCompletionIT extends PostgresIntegrationTest {

    private static final String PERSON = "Bearer " + TestTokens.person(UUID.randomUUID());
    private static final String SGT = "Bearer " + TestTokens.service("tasks.read", "tasks.write");
    private static final Duration LISTENER_DEADLINE = Duration.ofSeconds(10);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    private String activityId;

    @BeforeEach
    void startFromOneActivity() throws Exception {
        this.jdbc.update("DELETE FROM tasks");
        this.jdbc.update("DELETE FROM activities");
        this.jdbc.update("DELETE FROM objectives");
        this.jdbc.update("DELETE FROM plans");

        String planId = this.idOf(this.define("/api/v1/plans", """
            {"code": "2025-2030", "name": "Plan 2025-2030", "description": "Plan operativo institucional"}"""
        ));
        String objectiveId = this.idOf(this.define("/api/v1/plans/%s/objectives".formatted(planId), """
            {"name": "Monitoreo", "description": "Monitoreo de beneficiarios"}"""
        ));
        this.activityId = this.idOf(this.define(
            "/api/v1/plans/%s/objectives/%s/activities".formatted(planId, objectiveId),
            """
            {"name": "Seguimiento a beneficiarios", "description": "Visitas de seguimiento a beneficiarios",
             "measurementUnit": "beneficiarios con visita de seguimiento", "target": 2}"""
        ));
    }

    @Test
    void thePlanCompletesWithItsLastTask() throws Exception {
        String first = this.defineTask();
        String second = this.defineTask();

        this.complete(first);
        this.awaitOpenTasks(1);
        assertThat(this.completion()).containsExactly(false, false, false);

        this.complete(second);
        assertThat(this.awaitCompletion(List.of(true, true, true))).containsExactly(true, true, true);
    }

    @Test
    void aTaskDefinedAfterwardsOpensThePlanAgain() throws Exception {
        this.complete(this.defineTask());
        this.awaitCompletion(List.of(true, true, true));

        this.defineTask();

        assertThat(this.awaitCompletion(List.of(false, false, false))).containsExactly(false, false, false);
    }

    /** Defines a task under the activity and waits until the plan counts it open. @return its code */
    private String defineTask() throws Exception {
        int open = this.openTaskCount();
        String body = this.define("/api/v1/tasks", """
            {"activityId": "%s", "description": "Visita de seguimiento", "beneficiaryId": "DNI:12345678"}"""
            .formatted(this.activityId)
        ).andReturn()
            .getResponse()
            .getContentAsString();
        String code = JsonPath.read(body, "$.code");
        this.awaitOpenTasks(open + 1);

        return code;
    }

    /** Delivers and confirms the task the way sgt does. */
    private void complete(String taskCode) throws Exception {
        UUID submissionId = UUID.randomUUID();
        String submission = """
            {"submissionId": "%s", "executionDate": "2026-10-01"}""".formatted(submissionId);
        String reviewed = """
            {"submissionId": "%s"}""".formatted(submissionId);

        this.asSgt(post("/api/v1/tasks/{code}/delivery", taskCode), submission);
        this.asSgt(post("/api/v1/tasks/{code}/completion", taskCode), reviewed);
    }

    private void asSgt(MockHttpServletRequestBuilder request, String body) throws Exception {
        this.mockMvc.perform(request
                .header("Authorization", SGT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
            )
            .andExpect(status().isOk());
    }

    private ResultActions define(String path, String body) throws Exception {
        return this.mockMvc.perform(post(path)
                .header("Authorization", PERSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
            )
            .andExpect(status().isCreated());
    }

    private String idOf(ResultActions defined) throws Exception {
        String body = defined.andReturn()
            .getResponse()
            .getContentAsString();

        return JsonPath.read(body, "$.id");
    }

    /** @return whether the plan, its objective and its activity are complete, in that order */
    private List<Boolean> completion() {
        try {
            String plans = this.mockMvc.perform(get("/api/v1/plans").header("Authorization", PERSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

            return List.of(
                JsonPath.read(plans, "$.plans[0].completed"),
                JsonPath.read(plans, "$.plans[0].objectives[0].completed"),
                JsonPath.read(plans, "$.plans[0].objectives[0].activities[0].completed")
            );
        } catch (Exception e) {
            throw new IllegalStateException("Could not read the plans", e);
        }
    }

    private int openTaskCount() {
        return this.jdbc.queryForObject("SELECT count(*) FROM activity_open_tasks", Integer.class);
    }

    private List<Boolean> awaitCompletion(List<Boolean> expected) throws InterruptedException {
        Instant deadline = Instant.now().plus(LISTENER_DEADLINE);
        List<Boolean> answer = this.completion();
        while (!expected.equals(answer) && Instant.now().isBefore(deadline)) {
            Thread.sleep(50);
            answer = this.completion();
        }
        return answer;
    }

    private void awaitOpenTasks(int expected) throws InterruptedException {
        Instant deadline = Instant.now().plus(LISTENER_DEADLINE);
        while (this.openTaskCount() != expected && Instant.now().isBefore(deadline)) {
            Thread.sleep(50);
        }
        assertThat(this.openTaskCount()).isEqualTo(expected);
    }
}
