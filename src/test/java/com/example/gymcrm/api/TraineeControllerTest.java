package com.example.gymcrm.api;

import com.example.gymcrm.dto.UpdateTraineeRequest;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Basic controller tests focused on JSON shape & service wiring */
class TraineeControllerTest extends ApiTestSupport {

    private TraineeService traineeService;
    private TrainerService trainerService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        traineeService = mock(TraineeService.class);
        trainerService = mock(TrainerService.class);
        mockMvc = build(new TraineeController(traineeService, trainerService));
    }

    @Test
    void register_ok() throws Exception {
        var u = new User();
        u.setUsername("John.Smith");
        u.setPassword("p@ssW0rd");

        var saved = new Trainee();
        saved.setUser(u);

        when(traineeService.create(any(Trainee.class), eq("John"), eq("Smith"), eq(true)))
                .thenReturn(saved);

        String body = """
          {"firstName":"John","lastName":"Smith","dateOfBirth":"2000-05-05","address":"Main St 1"}
        """;

        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Smith"))
                .andExpect(jsonPath("$.password").value("p@ssW0rd"));

        verify(traineeService).create(any(Trainee.class), eq("John"), eq("Smith"), eq(true));
    }

    @Test
    void getProfile_ok() throws Exception {
        // trainee
        var tu = new User();
        tu.setUsername("john.smith");
        tu.setFirstName("John");
        tu.setLastName("Smith");
        tu.setActive(true);

        var trainee = new Trainee();
        trainee.setUser(tu);
        trainee.setAddress("Main 1");
        trainee.setDateOfBirth(LocalDate.parse("2000-05-05"));

        // one trainer in the list
        var trU = new User();
        trU.setUsername("jane.doe");
        trU.setFirstName("Jane");
        trU.setLastName("Doe");

        var trainer = new Trainer();
        trainer.setUser(trU);
        trainer.setSpecialization("Cardio"); // String in your model
        trainee.setTrainers(Set.of(trainer));

        when(traineeService.getByUsername(any(), eq("john.smith"))).thenReturn(trainee);

        mockMvc.perform(get("/api/trainees/{username}", "john.smith")
                        .header("X-Username", "john.smith")
                        .header("X-Password", "pw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.address").value("Main 1"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainers[0].username").value("jane.doe"))
                .andExpect(jsonPath("$.trainers[0].firstName").value("Jane"))
                .andExpect(jsonPath("$.trainers[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.trainers[0].specialization").value("Cardio"));
    }

    @Test
    void update_ok() throws Exception {
        var u = new User();
        u.setUsername("john.smith");
        u.setFirstName("John");
        u.setLastName("Smith");
        u.setActive(false);

        var updated = new Trainee();
        updated.setUser(u);
        updated.setDateOfBirth(LocalDate.parse("2001-01-01"));
        updated.setAddress("New addr");

        when(traineeService.updateProfile(any(), any(Trainee.class))).thenReturn(updated);

        String body = """
          {"firstName":"John","lastName":"Smith","dateOfBirth":"2001-01-01","address":"New addr","active":false}
        """;

        mockMvc.perform(put("/api/trainees/{username}", "john.smith")
                        .header("X-Username", "john.smith")
                        .header("X-Password", "pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.smith"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.address").value("New addr"))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void updateTrainers_ok() throws Exception {
        doNothing().when(traineeService).setTrainers(any(), eq("john.smith"), eq(List.of("jane.doe","bob.lee")));

        String body = """
          {"traineeUsername":"john.smith","trainers":["jane.doe","bob.lee"]}
        """;

        mockMvc.perform(put("/api/trainees/{username}/trainers", "john.smith")
                        .header("X-Username", "john.smith")
                        .header("X-Password", "pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(traineeService).setTrainers(any(), eq("john.smith"), eq(List.of("jane.doe","bob.lee")));
    }
}
