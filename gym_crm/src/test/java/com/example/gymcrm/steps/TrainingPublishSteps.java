package com.example.gymcrm.steps;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.dto.WorkloadEventRequest;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.integration.WorkloadEventPublisher;
import io.cucumber.java.en.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrainingPublishSteps {

    @Autowired private MockMvc mvc;

    @MockBean private TraineeDao traineeDao;
    @MockBean private TrainerDao trainerDao;
    @MockBean private TrainingTypeDao trainingTypeDao;
    @MockBean private TrainingDao trainingDao;

    @MockBean private WorkloadEventPublisher workloadPublisher;

    private int lastStatus = -1;

    private String lastTrainerUsername;
    private String lastAction;
    private int lastDuration;
    private LocalDate lastDate;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType type;

    @Given("trainee {string} exists and trainer {string} exists and training type {string} exists")
    public void setup(String traineeUsername, String trainerUsername, String typeName) {
        // trainee
        User tu = new User();
        tu.setId(11L);
        tu.setUsername(traineeUsername);
        tu.setFirstName("Trainee");
        tu.setLastName("One");
        tu.setActive(true);

        trainee = new Trainee();
        trainee.setId(101L);
        trainee.setUser(tu);

        // trainer
        User tru = new User();
        tru.setId(22L);
        tru.setUsername(trainerUsername);
        tru.setFirstName("Trainer");
        tru.setLastName("One");
        tru.setActive(true);

        trainer = new Trainer();
        trainer.setId(202L);
        trainer.setUser(tru);

        // training type
        type = new TrainingType();
        type.setName(typeName);

        // Controller uses findByUsername to map request entities
        when(traineeDao.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername(trainerUsername)).thenReturn(Optional.of(trainer));

        // Service create() uses ids + training type validation
        when(traineeDao.findById(101L)).thenReturn(Optional.of(trainee));
        when(trainerDao.findById(202L)).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName(typeName)).thenReturn(Optional.of(type));

        // save returns entity (simulate id assign)
        when(trainingDao.save(any(Training.class))).thenAnswer(inv -> {
            Training t = inv.getArgument(0);
            t.setId(999L);
            return t;
        });
    }

    @When("trainee {string} creates a training with trainer {string} type {string} duration {int} on date {string}")
    public void traineeCreatesTraining(String traineeUsername, String trainerUsername, String typeName, int duration, String date) throws Exception {
        // TrainingController expects AddTrainingRequest:
        // traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration, trainingType
        String json = """
                {
                  "traineeUsername": "%s",
                  "trainerUsername": "%s",
                  "trainingName": "Leg day",
                  "trainingDate": "%s",
                  "trainingDuration": %d,
                  "trainingType": "%s"
                }
                """.formatted(traineeUsername, trainerUsername, date, duration, typeName);

        var res = mvc.perform(
                        post("/api/trainings")
                                .with(user(traineeUsername).roles("TRAINEE"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andReturn();

        lastStatus = res.getResponse().getStatus();
    }

    @Then("response status should be {int}")
    public void statusShouldBe(int expected) {
        assertEquals(expected, lastStatus);
    }

    @Then("workload event should be published with trainer {string} action {string} duration {int} and date {string}")
    public void published(String trainerUsername, String action, int duration, String date) {
        ArgumentCaptor<WorkloadEventRequest> cap = ArgumentCaptor.forClass(WorkloadEventRequest.class);
        verify(workloadPublisher, times(1)).publish(cap.capture());

        WorkloadEventRequest ev = cap.getValue();
        assertNotNull(ev);

        assertEquals(trainerUsername, ev.getTrainerUsername());
        assertEquals(action, ev.getActionType());
        assertEquals(duration, ev.getTrainingDurationMinutes());
        assertEquals(LocalDate.parse(date), ev.getTrainingDate());
    }
}
