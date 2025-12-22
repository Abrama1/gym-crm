package com.example.gymcrm.api;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dto.AddTrainingRequest;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingController.class)
@Import(TestSecurityPermitConfig.class)
class TrainingControllerTest {

    @Autowired MockMvc mvc;

    @MockBean TrainingService trainingService;
    @MockBean TraineeDao traineeDao;
    @MockBean TrainerDao trainerDao;

    @Test
    @WithMockUser(username = "alice", roles = "TRAINEE")
    void create_asTrainee_self_ok() throws Exception {
        when(traineeDao.findByUsername("alice")).thenReturn(Optional.of(traineeWith("alice", "A", "A")));
        when(trainerDao.findByUsername("bob")).thenReturn(Optional.of(trainerWith("bob", "B", "B")));

        when(trainingService.create(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        String json = """
                {
                  "traineeUsername":"alice",
                  "trainerUsername":"bob",
                  "trainingName":"Morning Session",
                  "trainingDate":"2025-01-01",
                  "trainingDuration":60,
                  "trainingType":"Yoga"
                }
                """;

        mvc.perform(post("/api/trainings")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(trainingService).create(any(Training.class));
    }

    @Test
    @WithMockUser(username = "alice", roles = "TRAINEE")
    void listForTrainee_self_ok() throws Exception {
        Training tr = trainingForTraineeView(
                "Cardio",
                LocalDateTime.of(2025, 1, 2, 10, 0),
                "Yoga",
                45,
                trainerWith("bob", "Bob", "B")
        );

        when(trainingService.listForTrainee(eq("alice"), any()))
                .thenReturn(List.of(tr));

        mvc.perform(get("/api/trainings/trainee/alice")
                        .param("trainingType", "Yoga")
                        .param("otherPartyLike", "bob")
                        .param("from", "2025-01-01T00:00:00")
                        .param("to", "2025-01-10T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Cardio"))
                .andExpect(jsonPath("$[0].trainingType").value("Yoga"))
                .andExpect(jsonPath("$[0].trainingDuration").value(45))
                .andExpect(jsonPath("$[0].otherPartyName").value("Bob B"));
    }

    @Test
    @WithMockUser(username = "bob", roles = "TRAINER")
    void listForTrainer_self_ok() throws Exception {
        Training tr = trainingForTrainerView(
                "Strength",
                LocalDateTime.of(2025, 1, 3, 12, 0),
                "Box",
                90,
                traineeWith("alice", "Alice", "A")
        );

        when(trainingService.listForTrainer(eq("bob"), any()))
                .thenReturn(List.of(tr));

        mvc.perform(get("/api/trainings/trainer/bob")
                        .param("trainingType", "Box")
                        .param("otherPartyLike", "alice")
                        .param("from", "2025-01-01T00:00:00")
                        .param("to", "2025-01-10T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Strength"))
                .andExpect(jsonPath("$[0].trainingType").value("Box"))
                .andExpect(jsonPath("$[0].trainingDuration").value(90))
                .andExpect(jsonPath("$[0].otherPartyName").value("Alice A"));
    }

    // helpers
    private static Trainee traineeWith(String username, String first, String last) {
        User u = new User();
        u.setUsername(username);
        u.setFirstName(first);
        u.setLastName(last);

        Trainee t = new Trainee();
        t.setUser(u);
        return t;
    }

    private static Trainer trainerWith(String username, String first, String last) {
        User u = new User();
        u.setUsername(username);
        u.setFirstName(first);
        u.setLastName(last);

        Trainer tr = new Trainer();
        tr.setUser(u);
        return tr;
    }

    private static Training trainingForTraineeView(String name, LocalDateTime dateTime, String typeName, int duration, Trainer trainer) {
        TrainingType type = new TrainingType();
        type.setName(typeName);

        Training t = new Training();
        t.setTrainingName(name);
        t.setTrainingDate(dateTime);
        t.setTrainingType(type);
        t.setDurationMinutes(duration);
        t.setTrainer(trainer);
        return t;
    }

    private static Training trainingForTrainerView(String name, LocalDateTime dateTime, String typeName, int duration, Trainee trainee) {
        TrainingType type = new TrainingType();
        type.setName(typeName);

        Training t = new Training();
        t.setTrainingName(name);
        t.setTrainingDate(dateTime);
        t.setTrainingType(type);
        t.setDurationMinutes(duration);
        t.setTrainee(trainee);
        return t;
    }
}
