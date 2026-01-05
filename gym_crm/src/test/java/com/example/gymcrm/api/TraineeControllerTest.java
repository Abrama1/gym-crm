package com.example.gymcrm.api;

import com.example.gymcrm.dto.*;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeControllerTest {

    private TraineeService traineeService;
    private TrainerService trainerService;
    private TraineeController controller;

    @BeforeEach
    void setUp() {
        traineeService = mock(TraineeService.class);
        trainerService = mock(TrainerService.class);
        controller = new TraineeController(traineeService, trainerService);
    }

    @Test
    void register_returnsUsernameAndPlainPassword() {
        TraineeRegistrationRequest req = new TraineeRegistrationRequest();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setAddress("Tbilisi");
        req.setDateOfBirth(LocalDate.of(2000, 1, 1));

        User u = new User();
        u.setUsername("john.doe");
        u.setPlainPassword("p@ss123456");
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setActive(true);

        Trainee saved = new Trainee();
        saved.setId(1L);
        saved.setUser(u);

        when(traineeService.create(any(Trainee.class), eq("John"), eq("Doe"), eq(true)))
                .thenReturn(saved);

        RegistrationResponse res = controller.register(req);

        assertEquals("john.doe", res.getUsername());
        assertEquals("p@ss123456", res.getPassword());
        verify(traineeService, times(1)).create(any(Trainee.class), eq("John"), eq("Doe"), eq(true));
    }

    @Test
    void getProfile_mapsResponse() {
        Trainee t = buildTrainee("john", "John", "Doe");

        Trainer tr1 = buildTrainer("t1", "Ann", "Smith", "Yoga");
        Trainer tr2 = buildTrainer("t2", "Bob", "Brown", "Crossfit");

        t.getTrainers().addAll(List.of(tr1, tr2));

        when(traineeService.getByUsername("john")).thenReturn(t);

        TraineeProfileResponse res = controller.getProfile("john");

        assertEquals("john", res.getUsername());
        assertEquals("John", res.getFirstName());
        assertEquals("Doe", res.getLastName());
        assertEquals(2, res.getTrainers().size());
        assertEquals("Yoga", res.getTrainers().get(0).getSpecialization());
        assertEquals("Crossfit", res.getTrainers().get(1).getSpecialization());
    }

    @Test
    void update_updatesProfileAndUserFields() {
        String username = "john";

        UpdateTraineeRequest body = new UpdateTraineeRequest();
        body.setFirstName("NewFirst");
        body.setLastName("NewLast");
        body.setActive(false);
        body.setAddress("New Address");
        body.setDateOfBirth(LocalDate.of(1999, 12, 31));

        Trainee current = buildTrainee(username, "OldFirst", "OldLast");
        current.getUser().setActive(true);

        Trainee updated = buildTrainee(username, "OldFirst", "OldLast");
        updated.setAddress("New Address");
        updated.setDateOfBirth(LocalDate.of(1999, 12, 31));
        updated.getUser().setActive(true);

        when(traineeService.updateProfile(eq(username), any(Trainee.class))).thenReturn(updated);
        when(traineeService.getByUsername(username)).thenReturn(current);
        when(traineeService.update(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        TraineeProfileResponse res = controller.update(username, body);

        assertEquals(username, res.getUsername());
        assertEquals("NewFirst", res.getFirstName());
        assertEquals("NewLast", res.getLastName());
        assertEquals("New Address", res.getAddress());
        assertEquals(LocalDate.of(1999, 12, 31), res.getDateOfBirth());
        assertFalse(res.isActive());

        verify(traineeService).updateProfile(eq(username), any(Trainee.class));
        verify(traineeService).getByUsername(username);
        verify(traineeService).update(any(Trainee.class));
    }

    @Test
    void delete_callsService() {
        controller.delete("john");
        verify(traineeService).deleteByUsername("john");
    }

    @Test
    void availableTrainers_mapsList() {
        Trainer tr1 = buildTrainer("t1", "Ann", "Smith", "Yoga");
        Trainer tr2 = buildTrainer("t2", "Bob", "Brown", "Crossfit");

        when(trainerService.listNotAssignedToTrainee("john")).thenReturn(List.of(tr1, tr2));

        TrainersListResponse res = controller.availableTrainers("john");

        assertEquals(2, res.getItems().size());
        assertEquals("t1", res.getItems().get(0).getUsername());
        assertEquals("Yoga", res.getItems().get(0).getSpecialization());
        assertEquals("t2", res.getItems().get(1).getUsername());
    }

    @Test
    void setTrainers_callsServiceAndReturnsUpdatedList() {
        UpdateTraineeTrainersRequest req = new UpdateTraineeTrainersRequest();
        req.setTrainers(List.of("t1", "t2"));

        Trainee me = buildTrainee("john", "John", "Doe");
        me.getTrainers().add(buildTrainer("t1", "Ann", "Smith", "Yoga"));
        me.getTrainers().add(buildTrainer("t2", "Bob", "Brown", "Crossfit"));

        doNothing().when(traineeService).setTrainers("john", List.of("t1", "t2"));
        when(traineeService.getByUsername("john")).thenReturn(me);

        TrainersListResponse res = controller.setTrainers("john", req);

        verify(traineeService).setTrainers("john", List.of("t1", "t2"));
        verify(traineeService).getByUsername("john");

        assertEquals(2, res.getItems().size());
        assertEquals("t1", res.getItems().get(0).getUsername());
        assertEquals("t2", res.getItems().get(1).getUsername());
    }

    // --- helpers ---

    private static Trainee buildTrainee(String username, String first, String last) {
        User u = new User();
        u.setUsername(username);
        u.setFirstName(first);
        u.setLastName(last);
        u.setActive(true);

        Trainee t = new Trainee();
        t.setUser(u);
        return t;
    }

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
