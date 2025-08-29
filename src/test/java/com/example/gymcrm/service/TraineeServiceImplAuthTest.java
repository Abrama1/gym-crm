package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.exceptions.*;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplAuthTest {

    @Mock TraineeDao traineeDao;
    @Mock TrainerDao trainerDao;
    @Mock UserDao userDao;
    @Mock UsernameGenerator usernameGen;
    @Mock PasswordGenerator passwordGen;
    @Mock AuthService authService;

    TraineeService service;

    private Trainee me;
    private Credentials creds;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        service = new TraineeServiceImpl(traineeDao, trainerDao, userDao, usernameGen, passwordGen, authService);

        User u = new User(); u.setId(1L); u.setUsername("John.Smith"); u.setActive(true); u.setPassword("pw");
        me = new Trainee(); me.setId(10L); me.setUser(u); me.setAddress("A"); me.setDateOfBirth(LocalDate.of(2000,1,1));
        creds = new Credentials("John.Smith","pw");

        when(authService.authenticateTrainee(creds)).thenReturn(me);
        when(traineeDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void getByUsername_onlyOwnProfile() {
        when(traineeDao.findByUsername("John.Smith")).thenReturn(Optional.of(me));
        Trainee t = service.getByUsername(creds, "John.Smith");
        assertEquals(10L, t.getId());

        assertThrows(AuthFailedException.class, () -> service.getByUsername(creds, "Other.User"));
    }

    @Test
    void changePassword_updatesUser() {
        service.changePassword(creds, "new");
        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(cap.capture());
        assertEquals("new", cap.getValue().getPassword());
    }

    @Test
    void activate_deactivate_enforceNonIdempotent() {
        // deactivate from active
        service.deactivate(creds);
        assertFalse(me.getUser().isActive());
        verify(userDao).save(me.getUser());

        // second deactivate should error
        assertThrows(AlreadyDeactivatedException.class, () -> service.deactivate(creds));

        // activate back
        service.activate(creds);
        assertTrue(me.getUser().isActive());
        verify(userDao, times(2)).save(me.getUser());

        // second activate should error
        assertThrows(AlreadyActiveException.class, () -> service.activate(creds));
    }

    @Test
    void deleteByUsername_cascadesToUser() {
        service.deleteByUsername(creds, "John.Smith");
        verify(traineeDao).delete(me);
        verify(userDao).deleteById(1L);
    }

    @Test
    void setTrainers_replacesSet() {
        Trainer tr = new Trainer(); User u = new User(); u.setUsername("Jane.Doe"); tr.setUser(u);
        when(trainerDao.findByUsername("Jane.Doe")).thenReturn(Optional.of(tr));

        service.setTrainers(creds, "John.Smith", List.of("Jane.Doe"));
        assertEquals(1, me.getTrainers().size());
        verify(traineeDao).save(me);
    }
}
