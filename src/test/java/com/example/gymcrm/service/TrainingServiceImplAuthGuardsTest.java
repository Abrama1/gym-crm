package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplAuthGuardsTest {
    private TrainingServiceImpl svc;
    private AuthService auth;

    @BeforeEach
    void setUp() {
        svc = new TrainingServiceImpl(mock(TrainingDao.class), mock(TrainingTypeDao.class),
                mock(TraineeDao.class), mock(TrainerDao.class),
                auth = mock(AuthService.class), new SimpleMeterRegistry());
    }

    @Test
    void listForTrainee_otherUser_forbidden() {
        var me = new Trainee(); var u = new User(); u.setUsername("me"); me.setUser(u);
        when(auth.authenticateTrainee(any())).thenReturn(me);
        assertThrows(AuthFailedException.class, () -> svc.listForTrainee(creds("me","p"), "other", new TrainingCriteria()));
    }

    @Test
    void listForTrainer_otherUser_forbidden() {
        var me = new Trainer(); var u = new User(); u.setUsername("me"); me.setUser(u);
        when(auth.authenticateTrainer(any())).thenReturn(me);
        assertThrows(AuthFailedException.class, () -> svc.listForTrainer(creds("me","p"), "other", new TrainingCriteria()));
    }

    private Credentials creds(String u, String p) { var c = new Credentials(); c.setUsername(u); c.setPassword(p); return c; }
}
