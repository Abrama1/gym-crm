package com.example.gymcrm.api;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Verifies mapping and service delegation for trainings endpoints. */
class TrainingControllerTest extends ApiTestSupport {

    private TrainingService trainingService;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        trainingService = mock(TrainingService.class);
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);

        // Controller requires 3 args
        mockMvc = build(new TrainingController(trainingService, traineeDao, trainerDao));
    }

    @Test
    void add_ok() throws Exception {
        var trUser = new User(); trUser.setUsername("trainer.1");
        var teUser = new User(); teUser.setUsername("trainee.1");

        var trainer = new Trainer(); trainer.setId(20L); trainer.setUser(trUser);
        var trainee = new Trainee(); trainee.setId(10L); trainee.setUser(teUser);
        var type = new TrainingType(); type.setName("Cardio");

        var saved = new Training();
        saved.setId(100L);
        saved.setTrainingName("Morning Run");
        saved.setTrainingDate(LocalDateTime.parse("2024-12-01T08:30"));
        saved.setDurationMinutes(60);
        saved.setTrainer(trainer);
        saved.setTrainee(trainee);
        saved.setTrainingType(type);

        when(trainingService.create(any(Training.class))).thenReturn(saved);

        String body = """
          {
            "traineeUsername":"trainee.1",
            "trainerUsername":"trainer.1",
            "trainingName":"Morning Run",
            "trainingDate":"2024-12-01T08:30",
            "trainingDuration":60,
            "trainingType":"Cardio"
          }
        """;

        mockMvc.perform(post("/api/trainings")
                        .header("X-Username", "trainer.1")
                        .header("X-Password", "pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(trainingService).create(any(Training.class));
    }

    @Test
    void trainee_list_ok() throws Exception {
        var t = new Training();
        t.setTrainingName("Mobility");
        t.setTrainingDate(LocalDateTime.parse("2025-01-01T07:00"));
        var tp = new TrainingType(); tp.setName("Stretch");
        t.setTrainingType(tp);
        t.setDurationMinutes(30);
        var trU = new User(); trU.setFirstName("Jane"); trU.setLastName("Doe");
        var tr = new Trainer(); tr.setUser(trU); t.setTrainer(tr);

        when(trainingService.listForTrainee(any(), eq("trainee.1"), any()))
                .thenReturn(List.of(t));

        mockMvc.perform(get("/api/trainees/{username}/trainings", "trainee.1")
                        .header("X-Username", "trainee.1")
                        .header("X-Password", "pw")
                        .param("from", "2024-12-31T00:00")
                        .param("to", "2025-12-31T23:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Mobility"))
                .andExpect(jsonPath("$[0].trainingType").value("Stretch"))
                .andExpect(jsonPath("$[0].trainingDuration").value(30))
                .andExpect(jsonPath("$[0].trainerName").value("Jane Doe"));
    }

    @Test
    void trainer_list_ok() throws Exception {
        var t = new Training();
        t.setTrainingName("Intervals");
        t.setTrainingDate(LocalDateTime.parse("2025-02-01T09:00"));
        var tp = new TrainingType(); tp.setName("Cardio");
        t.setTrainingType(tp);
        t.setDurationMinutes(45);
        var teU = new User(); teU.setFirstName("John"); teU.setLastName("Smith");
        var te = new Trainee(); te.setUser(teU); t.setTrainee(te);

        when(trainingService.listForTrainer(any(), eq("trainer.1"), any()))
                .thenReturn(List.of(t));

        mockMvc.perform(get("/api/trainers/{username}/trainings", "trainer.1")
                        .header("X-Username", "trainer.1")
                        .header("X-Password", "pw")
                        .param("from", "2025-01-01T00:00")
                        .param("to", "2025-12-31T23:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Intervals"))
                .andExpect(jsonPath("$[0].trainingType").value("Cardio"))
                .andExpect(jsonPath("$[0].trainingDuration").value(45))
                .andExpect(jsonPath("$[0].traineeName").value("John Smith"));
    }
}
