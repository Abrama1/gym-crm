package com.example.gymcrm.api;

import com.example.gymcrm.dto.*;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class TraineeControllerTest {

    private MockMvc mockMvc;
    private TraineeService traineeService;
    private TrainerService trainerService;
    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        traineeService = mock(TraineeService.class);
        trainerService = mock(TrainerService.class);
        mockMvc = standaloneSetup(new TraineeController(traineeService, trainerService)).build();
    }

    @Test
    void register_ok() throws Exception {
        var u = new User();
        u.setUsername("john.smith");
        u.setPlainPassword("rawPw123"); // one-time password returned

        var saved = new Trainee();
        saved.setUser(u);

        when(traineeService.create(any(Trainee.class), eq("John"), eq("Smith"), eq(true)))
                .thenReturn(saved);

        var body = new TraineeRegistrationRequest();
        body.setFirstName("John");
        body.setLastName("Smith");
        body.setAddress("Main St 1");
        body.setDateOfBirth(LocalDate.parse("2000-05-05"));

        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.smith"))
                .andExpect(jsonPath("$.password").value("rawPw123"));
    }

    @Test
    void getProfile_ok() throws Exception {
        var u = new User();
        u.setUsername("trainee.1");
        u.setFirstName("John");
        u.setLastName("Smith");
        u.setActive(true);

        var t = new Trainee();
        t.setUser(u);
        t.setDateOfBirth(LocalDate.parse("2000-05-05"));
        t.setAddress("Main");
        // include one trainer to test mapping
        var trU = new User(); trU.setUsername("trainer.1"); trU.setFirstName("Jane"); trU.setLastName("Doe");
        var tr = new Trainer(); tr.setUser(trU); tr.setSpecialization("Cardio");
        t.setTrainers((java.util.Set<Trainer>) List.of(tr));

        when(traineeService.getByUsername(any(Credentials.class), eq("trainee.1"))).thenReturn(t);

        mockMvc.perform(get("/api/trainees/{u}", "trainee.1").with(user("trainee.1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("trainee.1"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.trainers[0].username").value("trainer.1"))
                .andExpect(jsonPath("$.trainers[0].specialization").value("Cardio"));
    }

    @Test
    void setTrainers_ok() throws Exception {
        var body = new UpdateTraineeTrainersRequest();
        body.setTrainers(List.of("trainer.1", "trainer.2"));

        // service is void + later we fetch profile again
        var u = new User(); u.setUsername("trainee.1"); u.setFirstName("John"); u.setLastName("Smith"); u.setActive(true);
        var t = new Trainee(); t.setUser(u);

        when(traineeService.getByUsername(any(Credentials.class), eq("trainee.1"))).thenReturn(t);

        mockMvc.perform(put("/api/trainees/{u}/trainers", "trainee.1")
                        .with(user("trainee.1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(body)))
                .andExpect(status().isOk());

        verify(traineeService).setTrainers(any(Credentials.class), eq("trainee.1"), eq(List.of("trainer.1", "trainer.2")));
    }
}
