package com.example.gymcrm.api;

import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.dto.*;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.TrainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class TrainerControllerTest {

    private MockMvc mockMvc;
    private TrainerService trainerService;
    private TrainingTypeDao trainingTypeDao;
    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        trainerService = mock(TrainerService.class);
        trainingTypeDao = mock(TrainingTypeDao.class);
        mockMvc = standaloneSetup(new TrainerController(trainerService, trainingTypeDao)).build();
    }

    @Test
    void register_ok() throws Exception {
        when(trainingTypeDao.findByName("Cardio")).thenReturn(Optional.of(new TrainingType()));

        var u = new User();
        u.setUsername("jane.doe");
        u.setPlainPassword("rawPwd#1"); // returned once

        var tr = new Trainer();
        tr.setUser(u);
        tr.setSpecialization("Cardio");

        when(trainerService.create(any(Trainer.class), eq("Jane"), eq("Doe"), eq(true)))
                .thenReturn(tr);

        var body = new TrainerRegistrationRequest();
        body.setFirstName("Jane");
        body.setLastName("Doe");
        body.setSpecialization("Cardio");

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane.doe"))
                .andExpect(jsonPath("$.password").value("rawPwd#1"));
    }

    @Test
    void getProfile_ok() throws Exception {
        var u = new User();
        u.setUsername("trainer.1");
        u.setFirstName("Jane");
        u.setLastName("Doe");
        u.setActive(true);

        var tr = new Trainer();
        tr.setUser(u);
        tr.setSpecialization("Cardio");

        // attach one trainee to exercise mapping
        var teU = new User(); teU.setUsername("trainee.1"); teU.setFirstName("John"); teU.setLastName("Smith");
        var te = new Trainee(); te.setUser(teU);
        tr.setTrainees((java.util.Set<Trainee>) List.of(te));

        when(trainerService.getByUsername(any(Credentials.class), eq("trainer.1")))
                .thenReturn(tr);

        mockMvc.perform(get("/api/trainers/{u}", "trainer.1").with(user("trainer.1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("trainer.1"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.specialization").value("Cardio"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.trainees[0].username").value("trainee.1"))
                .andExpect(jsonPath("$.trainees[0].firstName").value("John"))
                .andExpect(jsonPath("$.trainees[0].lastName").value("Smith"));
    }

    @Test
    void update_ok() throws Exception {
        var body = new UpdateTrainerRequest();
        body.setFirstName("Janet");
        body.setLastName("Doe");
        body.setActive(true);

        var savedU = new User();
        savedU.setUsername("trainer.1");
        savedU.setFirstName("Janet");
        savedU.setLastName("Doe");
        savedU.setActive(true);

        var saved = new Trainer();
        saved.setUser(savedU);
        saved.setSpecialization("Cardio");

        when(trainerService.updateProfile(any(Credentials.class), any(Trainer.class)))
                .thenReturn(saved);

        mockMvc.perform(put("/api/trainers/{u}", "trainer.1")
                        .with(user("trainer.1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("trainer.1"))
                .andExpect(jsonPath("$.firstName").value("Janet"))
                .andExpect(jsonPath("$.specialization").value("Cardio"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void activation_activate_ok() throws Exception {
        var body = new ActivationRequest();
        body.setActive(true);

        mockMvc.perform(patch("/api/trainers/{u}/activation", "trainer.1")
                        .with(user("trainer.1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(body)))
                .andExpect(status().isOk());

        verify(trainerService).activate(any(Credentials.class));
        verify(trainerService, never()).deactivate(any(Credentials.class));
    }

    @Test
    void activation_deactivate_ok() throws Exception {
        var body = new ActivationRequest();
        body.setActive(false);

        mockMvc.perform(patch("/api/trainers/{u}/activation", "trainer.1")
                        .with(user("trainer.1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(body)))
                .andExpect(status().isOk());

        verify(trainerService).deactivate(any(Credentials.class));
        verify(trainerService, never()).activate(any(Credentials.class));
    }
}
