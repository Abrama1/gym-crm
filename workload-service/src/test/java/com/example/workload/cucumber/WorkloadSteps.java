package com.example.workload.cucumber;

import com.example.workload.dto.ActionType;
import com.example.workload.dto.MonthSummaryResponse;
import com.example.workload.dto.WorkloadEventRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class WorkloadSteps {

    private final TestRestTemplate rest;

    private WorkloadEventRequest currentReq;
    private ResponseEntity<Void> lastPostResponse;

    public WorkloadSteps(TestRestTemplate rest) {
        this.rest = rest;
    }

    @Given("a workload event payload for trainer {string} with duration {int} on date {string} and action {string}")
    public void eventPayload(String username, int minutes, String date, String action) {
        WorkloadEventRequest req = new WorkloadEventRequest();
        req.setTrainerUsername(username);
        req.setTrainerFirstName("John");
        req.setTrainerLastName("Doe");
        req.setActive(true);
        req.setTrainingDate(LocalDate.parse(date));
        req.setTrainingDurationMinutes(minutes);
        req.setActionType(ActionType.valueOf(action));
        this.currentReq = req;
    }

    @Given("an invalid workload event payload missing trainerUsername")
    public void invalidMissingUsername() {
        WorkloadEventRequest req = new WorkloadEventRequest();
        req.setTrainerUsername(" "); // fails @NotBlank
        req.setTrainerFirstName("John");
        req.setTrainerLastName("Doe");
        req.setActive(true);
        req.setTrainingDate(LocalDate.parse("2025-01-01"));
        req.setTrainingDurationMinutes(10);
        req.setActionType(ActionType.ADD);
        this.currentReq = req;
    }

    @When("I POST the workload event")
    public void postEvent() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);


        h.set("X-Transaction-Id", "test-tx");

        HttpEntity<WorkloadEventRequest> entity = new HttpEntity<>(currentReq, h);
        lastPostResponse = rest.postForEntity("/api/workloads/events", entity, Void.class);
    }

    @Then("response status should be {int}")
    public void statusShouldBe(int code) {
        assertNotNull(lastPostResponse);
        assertEquals(code, lastPostResponse.getStatusCodeValue());
    }

    @Then("month summary for {string} year {int} month {int} should be {int}")
    public void monthSummaryShouldBe(String username, int year, int month, int expected) {
        ResponseEntity<MonthSummaryResponse> resp =
                rest.getForEntity("/api/workloads/{u}/months/{y}/{m}", MonthSummaryResponse.class, username, year, month);

        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals(expected, resp.getBody().getTotalMinutes());
    }
}
