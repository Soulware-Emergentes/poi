package lat.soulware.poi.planning.interfaces.rest.task;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import lat.soulware.poi.support.PostgresIntegrationTest;
import lat.soulware.poi.support.TestTokens;

/**
 * Drives a task through its lifecycle over HTTP, the way sgt does: listed while pending, taken off
 * the list by a delivery, put back by a rejection, and kept off for good by a confirmation.
 *
 * <p>Each test starts from one plan with two pending tasks under the same activity.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIT extends PostgresIntegrationTest {

    /** sgt, as staff's token for it says: allowed to read and write tasks. */
    private static final String SGT = "Bearer " + TestTokens.service("tasks.read", "tasks.write");


    private static final String FIRST_TASK = "2025-2030-01-03-0001";
    private static final String SECOND_TASK = "2025-2030-01-03-0002";
    private static final UUID SHEET_A = UUID.randomUUID();
    private static final UUID SHEET_B = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void seedOnePlanWithTwoPendingTasks() {
        this.jdbc.update("DELETE FROM tasks");
        this.jdbc.update("DELETE FROM activities");
        this.jdbc.update("DELETE FROM objectives");
        this.jdbc.update("DELETE FROM plans");

        UUID plan = UUID.randomUUID();
        UUID objective = UUID.randomUUID();
        UUID activity = UUID.randomUUID();
        this.jdbc.update("""
            INSERT INTO plans (id, code, name, description)
            VALUES (?, '2025-2030', 'Plan 2025-2030', 'Plan operativo institucional 2025-2030')""", plan);
        this.jdbc.update("""
            INSERT INTO objectives (id, plan_id, code, name, description)
            VALUES (?, ?, '2025-2030-01', 'Monitoreo', 'Monitoreo de beneficiarios')""", objective, plan);
        this.jdbc.update("""
            INSERT INTO activities (id, objective_id, code, name, description, measurement_unit, target)
            VALUES (?, ?, '2025-2030-01-03', 'Seguimiento a beneficiarios',
                'Visitas de seguimiento a beneficiarios', 'beneficiarios con visita de seguimiento', 2500)""",
            activity, objective
        );
        this.jdbc.update("INSERT INTO tasks (id, activity_id, code, description, kind, status, beneficiary_id) VALUES (?, ?, ?, ?, 'BENEFICIARY_FOLLOW_UP', 'PENDING', 'DNI:12345678')",
            UUID.randomUUID(), activity, FIRST_TASK, "Visita de seguimiento 1");
        this.jdbc.update("INSERT INTO tasks (id, activity_id, code, description, kind, status, beneficiary_id) VALUES (?, ?, ?, ?, 'BENEFICIARY_FOLLOW_UP', 'PENDING', 'DNI:12345678')",
            UUID.randomUUID(), activity, SECOND_TASK, "Visita de seguimiento 2");
    }

    @Test
    void theApiDocsAreServedWithoutAToken() throws Exception {
        this.mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paths['/api/v1/tasks'].get.tags[0]").value("task"))
            .andExpect(jsonPath("$.components.securitySchemes['bearer-token'].scheme").value("bearer"))
            .andExpect(jsonPath("$.security[0]['bearer-token']").isArray());
        this.mockMvc.perform(get("/swagger-ui/index.html"))
            .andExpect(status().isOk());
    }

    @Test
    void aCallWithoutATokenStaffIssuedForPoiIsRefused() throws Exception {
        List<String> refused = List.of(
            TestTokens.issue("http://elsewhere.test", TestTokens.AUDIENCE, "service", "sgt-service-dev", "tasks.read"),
            TestTokens.issue(TestTokens.ISSUER, "sgt-api-dev", "service", "sgt-service-dev", "tasks.read"),
            TestTokens.issue(TestTokens.ISSUER, TestTokens.AUDIENCE, "id", "sgt-service-dev", "tasks.read")
        );

        this.mockMvc.perform(get("/api/v1/tasks/pending"))
            .andExpect(status().isUnauthorized());
        for (String token : refused) {
            this.mockMvc.perform(get("/api/v1/tasks/pending").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void aPersonCannotCallTheTaskEndpoints() throws Exception {
        String person = "Bearer " + TestTokens.person(UUID.randomUUID());

        this.mockMvc.perform(get("/api/v1/tasks/pending").header("Authorization", person))
            .andExpect(status().isForbidden());
    }

    @Test
    void aServiceCannotCallWhatPeopleCall() throws Exception {
        String service = "Bearer " + TestTokens.service("tasks.read", "tasks.write");

        this.mockMvc.perform(get("/api/v1/plans").header("Authorization", service))
            .andExpect(status().isForbidden());
    }

    @Test
    void aServiceGrantedOnlyReadsCannotChangeATask() throws Exception {
        String readOnly = "Bearer " + TestTokens.service("tasks.read");

        this.mockMvc.perform(get("/api/v1/tasks/pending").header("Authorization", readOnly))
            .andExpect(status().isOk());
        this.mockMvc.perform(post("/api/v1/tasks/{code}/completion", FIRST_TASK)
                .header("Authorization", readOnly)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
            )
            .andExpect(status().isForbidden());
    }

    @Test
    void pendingTasksAreListedByCode() throws Exception {
        this.pending()
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tasks.length()").value(2))
            .andExpect(jsonPath("$.tasks[0].code").value(FIRST_TASK))
            .andExpect(jsonPath("$.tasks[0].measurementUnit").value("beneficiarios con visita de seguimiento"));
    }

    @Test
    void aDeliveryTakesTheTaskOffThePendingList() throws Exception {
        this.deliver(FIRST_TASK, SHEET_A)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("DELIVERED"))
            .andExpect(jsonPath("$.submissionId").value(SHEET_A.toString()));

        this.pending()
            .andExpect(jsonPath("$.tasks.length()").value(1))
            .andExpect(jsonPath("$.tasks[0].code").value(SECOND_TASK));
    }

    @Test
    void aSecondDeliveryForTheSameTaskIsRefused() throws Exception {
        this.deliver(FIRST_TASK, SHEET_A).andExpect(status().isOk());

        this.deliver(FIRST_TASK, SHEET_B).andExpect(status().isUnprocessableContent());
    }

    @Test
    void theSameDeliverySentAgainIsAccepted() throws Exception {
        this.deliver(FIRST_TASK, SHEET_A);

        this.deliver(FIRST_TASK, SHEET_A).andExpect(status().isOk());
    }

    @Test
    void aRejectedDeliveryPutsTheTaskBackOnThePendingList() throws Exception {
        this.deliver(FIRST_TASK, SHEET_A);

        this.review(FIRST_TASK, "reopening", SHEET_A).andExpect(status().isOk());

        this.pending().andExpect(jsonPath("$.tasks.length()").value(2));
        this.deliver(FIRST_TASK, SHEET_B).andExpect(status().isOk());
    }

    @Test
    void aRejectionSentAgainLeavesTheNextDeliveryAlone() throws Exception {
        this.deliver(FIRST_TASK, SHEET_A);
        this.review(FIRST_TASK, "reopening", SHEET_A);
        this.deliver(FIRST_TASK, SHEET_B);

        this.review(FIRST_TASK, "reopening", SHEET_A).andExpect(status().isOk());

        this.pending().andExpect(jsonPath("$.tasks.length()").value(1));
        this.review(FIRST_TASK, "completion", SHEET_B).andExpect(status().isOk());
    }

    @Test
    void aConfirmedDeliveryCompletesTheTaskForGood() throws Exception {
        this.deliver(FIRST_TASK, SHEET_A);

        this.review(FIRST_TASK, "completion", SHEET_A).andExpect(status().isOk());

        this.pending().andExpect(jsonPath("$.tasks.length()").value(1));
        this.review(FIRST_TASK, "completion", SHEET_A).andExpect(status().isOk());
        this.deliver(FIRST_TASK, SHEET_B).andExpect(status().isUnprocessableContent());
    }

    @Test
    void aTaskWithoutADeliveryCannotBeReviewed() throws Exception {
        this.review(FIRST_TASK, "completion", SHEET_A).andExpect(status().isUnprocessableContent());
    }

    @Test
    void aTaskIsReadWithItsBeneficiary() throws Exception {
        this.mockMvc.perform(get("/api/v1/tasks/{code}", FIRST_TASK).header("Authorization", SGT))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.beneficiaryId").value("DNI:12345678"));
    }

    @Test
    void relinkingOntoAPendingTaskMovesTheDelivery() throws Exception {
        this.deliver(FIRST_TASK, SHEET_A);

        this.relink(SECOND_TASK, FIRST_TASK, SHEET_A)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.submissionId").doesNotExist());

        this.pending()
            .andExpect(jsonPath("$.tasks.length()").value(1))
            .andExpect(jsonPath("$.tasks[0].code").value(FIRST_TASK));
    }

    @Test
    void relinkingOntoADeliveredTaskSwapsTheDeliveries() throws Exception {
        this.deliver(FIRST_TASK, SHEET_A);
        this.deliver(SECOND_TASK, SHEET_B);

        this.relink(SECOND_TASK, FIRST_TASK, SHEET_A)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.submissionId").value(SHEET_B.toString()));

        this.review(FIRST_TASK, "completion", SHEET_B).andExpect(status().isOk());
        this.review(SECOND_TASK, "completion", SHEET_A).andExpect(status().isOk());
    }

    @Test
    void aCompletedTaskRefusesARelink() throws Exception {
        this.deliver(FIRST_TASK, SHEET_A);
        this.deliver(SECOND_TASK, SHEET_B);
        this.review(SECOND_TASK, "completion", SHEET_B);

        this.relink(SECOND_TASK, FIRST_TASK, SHEET_A).andExpect(status().isUnprocessableContent());
    }

    @Test
    void anUnknownTaskCodeIsNotFound() throws Exception {
        this.deliver("2025-2030-01-03-0099", SHEET_A).andExpect(status().isNotFound());
    }

    private ResultActions pending() throws Exception {
        return this.mockMvc.perform(get("/api/v1/tasks/pending").header("Authorization", SGT));
    }

    private ResultActions deliver(String taskCode, UUID submissionId) throws Exception {
        String delivery = """
            {"submissionId": "%s", "executionDate": "2026-10-01"}"""
            .formatted(submissionId);

        return this.mockMvc.perform(post("/api/v1/tasks/{code}/delivery", taskCode)
            .header("Authorization", SGT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(delivery)
        );
    }

    private ResultActions relink(String taskCode, String fromTaskCode, UUID submissionId) throws Exception {
        String relinked = """
            {"submissionId": "%s", "fromTaskCode": "%s"}""".formatted(submissionId, fromTaskCode);

        return this.mockMvc.perform(post("/api/v1/tasks/{code}/delivery/relink", taskCode)
            .header("Authorization", SGT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(relinked)
        );
    }

    private ResultActions review(String taskCode, String outcome, UUID submissionId) throws Exception {
        String reviewed = """
            {"submissionId": "%s"}""".formatted(submissionId);

        return this.mockMvc.perform(post("/api/v1/tasks/{code}/{outcome}", taskCode, outcome)
            .header("Authorization", SGT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(reviewed)
        );
    }
}
