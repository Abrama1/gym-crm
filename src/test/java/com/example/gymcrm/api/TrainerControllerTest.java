package com.example.gymcrm.api;

import com.example.gymcrm.dto.UpdateTrainerRequest;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TrainerControllerTest extends ApiTestSupport {

    private TrainerService trainerService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        trainerService = mock(TrainerService.class);
        mockMvc = build(new TrainerController(trainerService, null));
    }

    @Test
    void register_ok() throws Exception {
        var u = new User();
        u.setUsername("Jane.Doe");
        u.setPassword("p@ss");

        var saved = new Trainer();
        saved.setUser(u);

        when(trainerService.create(any(Trainer.class), eq("Jane"), eq("Doe"), eq(true)))
                .thenReturn(saved);

        String body = """
          {"firstName":"Jane","lastName":"Doe","specialization":"Cardio"}
        """;

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Jane.Doe"))
                .andExpect(jsonPath("$.password").value("p@ss"));
    }

    @Test
    void getProfile_ok() throws Exception {
        var u = new User();
        u.setUsername("jane.doe");
        u.setFirstName("Jane");
        u.setLastName("Doe");
        u.setActive(true);

        var trainer = new Trainer();
        trainer.setUser(u);
        trainer.setSpecialization("Cardio");

        when(trainerService.getByUsername(any(), eq("jane.doe"))).thenReturn(trainer);

        mockMvc.perform(get("/api/trainers/{username}", "jane.doe")
                        .header("X-Username", "jane.doe")
                        .header("X-Password", "pw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.specialization").value("Cardio"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void update_ok() throws Exception {
        var u = new User();
        u.setUsername("jane.doe");
        u.setFirstName("Jane");
        u.setLastName("Doe");
        u.setActive(false);

        var updated = new Trainer();
        updated.setUser(u);
        updated.setSpecialization("Cardio");

        when(trainerService.updateProfile(any(), any(Trainer.class))).thenReturn(updated);

        String body = """
          {"firstName":"Jane","lastName":"Doe","active":false}
        """;

        mockMvc.perform(put("/api/trainers/{username}", "jane.doe")
                        .header("X-Username", "jane.doe")
                        .header("X-Password", "pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane.doe"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.specialization").value("Cardio"))
                .andExpect(jsonPath("$.isActive").value(false));
    }
}
