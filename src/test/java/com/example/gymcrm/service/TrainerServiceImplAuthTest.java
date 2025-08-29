package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.exceptions.*;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplAuthTest {

    @Mock TrainerDao trainerDao;
    @Mock UserDao userDao;
    @Mock UsernameGenerator usernameGen;
    @Mock PasswordGenerator passwordGen;
    @Mock AuthService authService;

    TrainerService service;
    private Trainer me;
    private Credentials creds;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        service = new TrainerServiceImpl(trainerDao, userDao, usernameGen, passwordGen, authService);

        User u = new User(); u.setId(2L); u.setUsername("Jane.Doe"); u.setActive(true); u.setPassword("pw");
        me = new Trainer(); me.setId(20L); me.setUser(u); me.setSpecialization("Strength");
        creds = new Credentials("Jane.Doe","pw");

        when(authService.authenticateTrainer(creds)).thenReturn(me);
        when(trainerDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void getByUsername_onlyOwnProfile() {
        when(trainerDao.findByUsername("Jane.Doe")).thenReturn(Optional.of(me));
        Trainer t = service.getByUsername(creds, "Jane.Doe");
        assertEquals(20L, t.getId());
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
    void activate_deactivate_nonIdempotent() {
        service.deactivate(creds);
        assertFalse(me.getUser().isActive());
        verify(userDao).save(me.getUser());
        assertThrows(AlreadyDeactivatedException.class, () -> service.deactivate(creds));

        service.activate(creds);
        assertTrue(me.getUser().isActive());
        verify(userDao, times(2)).save(me.getUser());
        assertThrows(AlreadyActiveException.class, () -> service.activate(creds));
    }

    @Test
    void listNotAssignedToTrainee_requiresAuth() {
        when(trainerDao.listNotAssignedToTrainee("John.Smith")).thenReturn(List.of(me));
        var res = service.listNotAssignedToTrainee(creds, "John.Smith");
        assertEquals(1, res.size());
    }
}
