package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock UserDao userDao;
    @Mock TraineeDao traineeDao;
    @Mock TrainerDao trainerDao;

    AuthService service;

    @BeforeEach
    void setup(){ MockitoAnnotations.openMocks(this); service = new AuthServiceImpl(userDao, traineeDao, trainerDao); }

    @Test
    void authenticateTrainee_success() {
        User u = user("John.Smith","pw", true, 1L);
        Trainee t = new Trainee(); t.setUser(u);

        when(userDao.findByUsername("John.Smith")).thenReturn(Optional.of(u));
        when(traineeDao.findByUserId(1L)).thenReturn(Optional.of(t));

        var res = service.authenticateTrainee(new Credentials("John.Smith","pw"));
        assertSame(t, res);
    }

    @Test
    void authenticateTrainer_passwordMismatch_fails() {
        User u = user("Jane.Doe","pw", true, 2L);
        when(userDao.findByUsername("Jane.Doe")).thenReturn(Optional.of(u));
        assertThrows(AuthFailedException.class, () -> service.authenticateTrainer(new Credentials("Jane.Doe","nope")));
    }

    @Test
    void authenticateTrainee_inactive_fails() {
        User u = user("John.Smith","pw", false, 1L);
        when(userDao.findByUsername("John.Smith")).thenReturn(Optional.of(u));
        assertThrows(AuthFailedException.class, () -> service.authenticateTrainee(new Credentials("John.Smith","pw")));
    }

    private User user(String un, String pw, boolean active, Long id){
        User u = new User(); u.setId(id); u.setUsername(un); u.setPassword(pw); u.setActive(active);
        return u;
    }
}
