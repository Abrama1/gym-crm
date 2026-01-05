package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.service.impl.AuthServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private UserDao userDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private PasswordEncoder encoder;
    private AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);
        encoder = mock(PasswordEncoder.class);
        service = new AuthServiceImpl(userDao, traineeDao, trainerDao, new SimpleMeterRegistry(), encoder);
    }

    @Test
    void authenticateTrainee_ok() {
        var u = new User(); u.setId(1L); u.setUsername("john"); u.setPassword("$hash"); u.setActive(true);
        var t = new Trainee(); t.setUser(u);

        when(userDao.findByUsername("john")).thenReturn(Optional.of(u));
        when(encoder.matches("pw", "$hash")).thenReturn(true);
        when(traineeDao.findByUserId(1L)).thenReturn(Optional.of(t));

        var res = service.authenticateTrainee(new Credentials("john", "pw"));
        assertSame(t, res);
    }

    @Test
    void authenticateTrainee_badPassword() {
        var u = new User(); u.setId(1L); u.setUsername("john"); u.setPassword("$hash"); u.setActive(true);
        when(userDao.findByUsername("john")).thenReturn(Optional.of(u));
        when(encoder.matches("bad", "$hash")).thenReturn(false);

        assertThrows(AuthFailedException.class,
                () -> service.authenticateTrainee(new Credentials("john", "bad")));
    }

    @Test
    void authenticateTrainer_userInactive() {
        var u = new User(); u.setId(2L); u.setUsername("jane"); u.setPassword("$hash"); u.setActive(false);
        when(userDao.findByUsername("jane")).thenReturn(Optional.of(u));

        assertThrows(AuthFailedException.class,
                () -> service.authenticateTrainer(new Credentials("jane", "pw")));
    }

    @Test
    void authenticateTrainer_missing_profile() {
        var u = new User(); u.setId(3L); u.setUsername("sam"); u.setPassword("$h"); u.setActive(true);
        when(userDao.findByUsername("sam")).thenReturn(Optional.of(u));
        when(encoder.matches("pw", "$h")).thenReturn(true);
        when(trainerDao.findByUserId(3L)).thenReturn(Optional.empty());

        assertThrows(AuthFailedException.class,
                () -> service.authenticateTrainer(new Credentials("sam", "pw")));
    }
}
