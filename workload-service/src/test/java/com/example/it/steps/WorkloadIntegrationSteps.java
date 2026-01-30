package com.example.it.steps;

import com.example.gymcrm.dto.WorkloadEventRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.awaitility.Awaitility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkloadIntegrationSteps {

    private final RestTemplate rest = new RestTemplate();

    private JmsTemplate jms;
    private String queueName;

    @Given("microservices are running")
    public void microservicesAreRunning() {
        com.example.it.ItEnvironment.ensureStarted();

        String brokerUrl = System.getProperty("it.brokerUrl");
        if (brokerUrl == null) {
            throw new IllegalStateException("Missing system property it.brokerUrl. Set it in ItEnvironment.ensureStarted().");
        }

        ConnectionFactory cf = new ActiveMQConnectionFactory("admin", "admin", brokerUrl);
        jms = new JmsTemplate(cf);

        queueName = "workload.events";
    }

    @When("a workload ADD event is published for trainer {string} with {int} minutes on {string}")
    public void publishAdd(String trainerUsername, int minutes, String dateIso) {
        publish(trainerUsername, minutes, dateIso, "ADD");
    }

    @When("a workload DELETE event is published for trainer {string} with {int} minutes on {string}")
    public void publishDelete(String trainerUsername, int minutes, String dateIso) {
        publish(trainerUsername, minutes, dateIso, "DELETE");
    }

    @When("an invalid workload event is published missing trainer username")
    public void invalidEventMissingUsername() {
        WorkloadEventRequest req = new WorkloadEventRequest();
        req.setTrainerUsername("");
        req.setTrainerFirstName("Bad");
        req.setTrainerLastName("Msg");
        req.setActive(true);
        req.setTrainingDate(LocalDate.parse("2026-01-10"));
        req.setTrainingDurationMinutes(10);
        req.setActionType("ADD");

        jms.convertAndSend(queueName, req);
    }

    @Then("workload-service month summary for {string} in {int}-{int} should be {int} minutes")
    public void assertMonth(String trainerUsername, int year, int month, int expected) {
        String url = com.example.it.ItEnvironment.workloadBaseUrl()
                + "/api/workloads/" + trainerUsername + "/months/" + year + "/" + month;

        Awaitility.await()
                .atMost(Duration.ofSeconds(8))
                .pollInterval(Duration.ofMillis(250))
                .untilAsserted(() -> {
                    ResponseEntity<Map> resp = rest.getForEntity(url, Map.class);
                    Object total = resp.getBody().get("totalMinutes");
                    int actual = (total instanceof Number) ? ((Number) total).intValue() : Integer.parseInt(total.toString());
                    assertEquals(expected, actual);
                });
    }

    private void publish(String trainerUsername, int minutes, String dateIso, String action) {
        WorkloadEventRequest req = new WorkloadEventRequest();
        req.setTrainerUsername(trainerUsername);
        req.setTrainerFirstName("TFirst");
        req.setTrainerLastName("TLast");
        req.setActive(true);
        req.setTrainingDate(LocalDate.parse(dateIso));
        req.setTrainingDurationMinutes(minutes);
        req.setActionType(action);

        jms.convertAndSend(queueName, req);
    }
}
