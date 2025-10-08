package com.example.gymcrm.api;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dto.AddTrainingRequest;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class TrainingControllerTest {

    private TrainingService trainingService;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        trainingService = mock(TrainingService.class);
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);
        // NOTE: Controller now expects 3 args
        mockMvc = standaloneSetup(new TrainingController(trainingService, traineeDao, trainerDao)).build();
    }

    @Test
    void add_ok() throws Exception {
        // users
        var trUser = new User(); trUser.setUsername("trainer.1"); trUser.setFirstName("Jane"); trUser.setLastName("Doe");
        var teUser = new User(); teUser.setUsername("trainee.1"); teUser.setFirstName("John"); teUser.setLastName("Smith");

        // dao lookups by username -> return entities with IDs (controller needs IDs for service.create)
        var trainer = new Trainer(); trainer.setId(20L); trainer.setUser(trUser);
        var trainee = new Trainee(); trainee.setId(10L); trainee.setUser(teUser);
        when(trainerDao.findByUsername("trainer.1")).thenReturn(Optional.of(trainer));
        when(traineeDao.findByUsername("trainee.1")).thenReturn(Optional.of(trainee));

        // service create returns saved training
        var type = new TrainingType(); type.setName("Cardio");
        var saved = new Training();
        saved.setId(100L);
        saved.setTrainingName("Morning Run");
        saved.setTrainingDate(LocalDateTime.parse("2025-01-01T08:30"));
        saved.setDurationMinutes(60);
        saved.setTrainer(trainer);
        saved.setTrainee(trainee);
        saved.setTrainingType(type);
        when(trainingService.create(any(Training.class))).thenReturn(saved);

        var body = new AddTrainingRequest();
        body.setTraineeUsername("trainee.1");
        body.setTrainerUsername("trainer.1");
        body.setTrainingName("Morning Run");
        body.setTrainingDate(LocalDate.parse("2025-01-01")); // LocalDate in request
        body.setTrainingDuration(60);
        body.setTrainingType("Cardio");

        mockMvc.perform(post("/api/trainings")
                        .with(user("trainer.1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(body)))
                .andExpect(status().isOk());

        verify(trainerDao).findByUsername("trainer.1");
        verify(traineeDao).findByUsername("trainee.1");
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

        when(trainingService.listForTrainee(any(Credentials.class), eq("trainee.1"), any(TrainingCriteria.class)))
                .thenReturn(List.of(t));

        mockMvc.perform(get("/api/trainees/{u}/trainings", "trainee.1")
                        .with(user("trainee.1"))
                        .param("from", "2024-12-31T00:00")
                        .param("to", "2025-12-31T23:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Mobility"))
                .andExpect(jsonPath("$[0].trainingType").value("Stretch"))
                .andExpect(jsonPath("$[0].trainingDuration").value(30))
                .andExpect(jsonPath("$[0].otherPartyName").value("Jane Doe"))
                .andExpect(jsonPath("$[0].trainingDate").value("2025-01-01")); // controller maps to LocalDate
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

        when(trainingService.listForTrainer(any(Credentials.class), eq("trainer.1"), any(TrainingCriteria.class)))
                .thenReturn(List.of(t));

        mockMvc.perform(get("/api/trainers/{u}/trainings", "trainer.1")
                        .with(user("trainer.1"))
                        .param("from", "2025-01-01T00:00")
                        .param("to", "2025-12-31T23:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Intervals"))
                .andExpect(jsonPath("$[0].trainingType").value("Cardio"))
                .andExpect(jsonPath("$[0].trainingDuration").value(45))
                .andExpect(jsonPath("$[0].otherPartyName").value("John Smith"))
                .andExpect(jsonPath("$[0].trainingDate").value("2025-02-01"));
    }
}
