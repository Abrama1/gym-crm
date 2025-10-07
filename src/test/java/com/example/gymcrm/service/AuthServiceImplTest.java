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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    private UserDao userDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private SimpleMeterRegistry registry;
    private AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);
        registry = new SimpleMeterRegistry();
        service = new AuthServiceImpl(userDao, traineeDao, trainerDao, registry);
    }

    @Test
    void authenticateTrainee_success_marksSuccessMetric() {
        var u = new User(); u.setId(1L); u.setUsername("john"); u.setPassword("pw"); u.setActive(true);
        when(userDao.findByUsername("john")).thenReturn(Optional.of(u));
        when(traineeDao.findByUserId(1L)).thenReturn(Optional.of(new Trainee()));

        assertNotNull(service.authenticateTrainee(creds("john","pw")));

        assertEquals(1.0, registry.find("gym_auth_attempts_total")
                .tags("role","trainee","outcome","success").counter().count(), 1e-9);
    }

    @Test
    void authenticateTrainer_wrongPassword_marksFailureMetric() {
        var u = new User(); u.setId(2L); u.setUsername("ann"); u.setPassword("pw"); u.setActive(true);
        when(userDao.findByUsername("ann")).thenReturn(Optional.of(u));

        assertThrows(AuthFailedException.class, () -> service.authenticateTrainer(creds("ann","bad")));

        assertEquals(1.0, registry.find("gym_auth_attempts_total")
                .tags("role","trainer","outcome","failure").counter().count(), 1e-9);
    }

    private Credentials creds(String u, String p) {
        var c = new Credentials(); c.setUsername(u); c.setPassword(p); return c;
    }
}
