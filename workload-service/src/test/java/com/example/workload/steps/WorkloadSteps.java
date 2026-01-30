package com.example.workload.steps;

import com.example.workload.dto.ActionType;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.mongo.TrainerWorkloadRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WorkloadSteps extends MongoTestContainerConfig {

    @Autowired private TestRestTemplate rest;
    @Autowired private TrainerWorkloadRepository repo;

    private ResponseEntity<String> lastResponse;

    @Before
    public void beforeEach() {
        lastResponse = null;
    }

    @Given("workload repository is empty")
    public void repoEmpty() {
        repo.deleteAll();
        assertEquals(0, repo.count());
    }

    @When("I POST workload event:")
    public void postEvent(DataTable table) {
        Map<String, String> m = table.asMap(String.class, String.class);

        WorkloadEventRequest req = new WorkloadEventRequest();
        req.setTrainerUsername(m.get("trainerUsername"));
        req.setTrainerFirstName(m.get("trainerFirstName"));
        req.setTrainerLastName(m.get("trainerLastName"));
        req.setActive(Boolean.parseBoolean(m.get("active")));
        req.setTrainingDate(LocalDate.parse(m.get("trainingDate")));
        req.setTrainingDurationMinutes(Integer.parseInt(m.get("trainingDurationMinutes")));
        req.setActionType(ActionType.valueOf(m.get("actionType")));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        lastResponse = rest.postForEntity("/api/workloads/events", new HttpEntity<>(req, headers), String.class);
        assertNotNull(lastResponse);
        assertEquals(200, lastResponse.getStatusCode().value(), "Expected 200 OK");
    }

    @When("I POST workload event with missing trainerUsername")
    public void postInvalidMissingUsername() {
        WorkloadEventRequest req = new WorkloadEventRequest();
        req.setTrainerFirstName("John");
        req.setTrainerLastName("Trainer");
        req.setActive(true);
        req.setTrainingDate(LocalDate.parse("2026-01-01"));
        req.setTrainingDurationMinutes(10);
        req.setActionType(ActionType.ADD);

        lastResponse = rest.postForEntity("/api/workloads/events", req, String.class);
        assertNotNull(lastResponse);
    }

    @Then("response status should be {int}")
    public void statusShouldBe(int code) {
        assertNotNull(lastResponse);
        assertEquals(code, lastResponse.getStatusCode().value());
    }

    @Then("workload month minutes for {string} in {int}/{int} should be {int}")
    public void monthMinutesShouldBe(String username, int year, int month, int expected) {
        ResponseEntity<String> resp = rest.getForEntity(
                "/api/workloads/{u}/months/{y}/{m}",
                String.class,
                username, year, month
        );
        assertEquals(200, resp.getStatusCode().value());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().contains("\"totalMinutes\":" + expected),
                "Body did not contain expected minutes. Body=" + resp.getBody());
    }
}
