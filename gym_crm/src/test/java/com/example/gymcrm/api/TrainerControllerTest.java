package com.example.gymcrm.api;

import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.dto.*;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerControllerTest {

    private TrainerService trainerService;
    private TrainingTypeDao trainingTypeDao;
    private TrainerController controller;

    @BeforeEach
    void setUp() {
        trainerService = mock(TrainerService.class);
        trainingTypeDao = mock(TrainingTypeDao.class);
        controller = new TrainerController(trainerService, trainingTypeDao);
    }

    @Test
    void register_validatesTrainingType_andReturnsPlainPassword() {
        TrainerRegistrationRequest req = new TrainerRegistrationRequest();
        req.setFirstName("Ann");
        req.setLastName("Smith");
        req.setSpecialization("Yoga");

        when(trainingTypeDao.findByName("Yoga")).thenReturn(Optional.of(new TrainingType()));

        User u = new User();
        u.setUsername("ann.smith");
        u.setPlainPassword("pw123");
        u.setFirstName("Ann");
        u.setLastName("Smith");
        u.setActive(true);

        Trainer saved = new Trainer();
        saved.setId(5L);
        saved.setUser(u);
        saved.setSpecialization("Yoga"); // IMPORTANT: String

        when(trainerService.create(any(Trainer.class), eq("Ann"), eq("Smith"), eq(true)))
                .thenReturn(saved);

        RegistrationResponse res = controller.register(req);

        assertEquals("ann.smith", res.getUsername());
        assertEquals("pw123", res.getPassword());

        verify(trainingTypeDao).findByName("Yoga");
        verify(trainerService).create(any(Trainer.class), eq("Ann"), eq("Smith"), eq(true));
    }

    @Test
    void register_unknownTrainingType_throws() {
        TrainerRegistrationRequest req = new TrainerRegistrationRequest();
        req.setFirstName("Ann");
        req.setLastName("Smith");
        req.setSpecialization("UnknownType");

        when(trainingTypeDao.findByName("UnknownType")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> controller.register(req));
        verify(trainerService, never()).create(any(), any(), any(), anyBoolean());
    }

    @Test
    void getProfile_mapsResponse() {
        Trainer tr = buildTrainer("ann", "Ann", "Smith", "Yoga");
        when(trainerService.getByUsername("ann")).thenReturn(tr);

        TrainerProfileResponse res = controller.getProfile("ann");

        assertEquals("ann", res.getUsername());
        assertEquals("Ann", res.getFirstName());
        assertEquals("Smith", res.getLastName());
        assertEquals("Yoga", res.getSpecialization());
    }

    @Test
    void update_updatesNameAndActive_only() {
        UpdateTrainerRequest body = new UpdateTrainerRequest();
        body.setFirstName("New");
        body.setLastName("Name");
        body.setActive(true);

        Trainer me = buildTrainer("ann", "Ann", "Smith", "Yoga");
        me.getUser().setActive(false);

        when(trainerService.getByUsername("ann")).thenReturn(me);
        when(trainerService.update(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        TrainerProfileResponse res = controller.update("ann", body);

        assertEquals("ann", res.getUsername());
        assertEquals("New", res.getFirstName());
        assertEquals("Name", res.getLastName());
        assertEquals("Yoga", res.getSpecialization()); // unchanged
        assertTrue(res.isActive());

        verify(trainerService).getByUsername("ann");
        verify(trainerService).update(any(Trainer.class));
    }

    @Test
    void activation_activeTrue_callsActivate() {
        ActivationRequest req = new ActivationRequest();
        req.setActive(true);

        ResponseEntity<Void> res = controller.activation("ann", req);

        assertEquals(200, res.getStatusCodeValue());
        verify(trainerService).activate("ann");
        verify(trainerService, never()).deactivate(anyString());
    }

    @Test
    void activation_activeFalse_callsDeactivate() {
        ActivationRequest req = new ActivationRequest();
        req.setActive(false);

        ResponseEntity<Void> res = controller.activation("ann", req);

        assertEquals(200, res.getStatusCodeValue());
        verify(trainerService).deactivate("ann");
        verify(trainerService, never()).activate(anyString());
    }

    // --- helper ---

    private static Trainer buildTrainer(String username, String first, String last, String specialization) {
        User u = new User();
        u.setUsername(username);
        u.setFirstName(first);
        u.setLastName(last);
        u.setActive(true);

        Trainer tr = new Trainer();
        tr.setUser(u);
        tr.setSpecialization(specialization); // IMPORTANT: always String
        return tr;
    }
}
