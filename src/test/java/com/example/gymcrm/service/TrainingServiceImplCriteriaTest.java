package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplCriteriaTest {
    private TrainingDao trainingDao;
    private TrainingServiceImpl svc;
    private AuthService auth;
    private SimpleMeterRegistry reg;

    @BeforeEach
    void setUp() {
        trainingDao = mock(TrainingDao.class);
        reg = new SimpleMeterRegistry();
        svc = new TrainingServiceImpl(trainingDao, mock(TrainingTypeDao.class),
                mock(TraineeDao.class), mock(TrainerDao.class),
                auth = mock(AuthService.class), reg);
    }

    @Test
    void listForTrainee_recordsTimerAndReturns() {
        var me = new Trainee(); var u = new User(); u.setUsername("john"); me.setUser(u);
        when(auth.authenticateTrainee(any())).thenReturn(me);
        when(trainingDao.listForTrainee(anyString(), any(), any(), any(), any()))
                .thenReturn(List.of(new Training()));

        var out = svc.listForTrainee(creds("john","p"), "john", new TrainingCriteria());
        assertEquals(1, out.size());
        assertEquals(1L, reg.find("gymcrm.trainings.list").tags("side","trainee").timer().count());
    }

    @Test
    void listForTrainer_recordsTimerAndReturns() {
        var me = new Trainer(); var u = new User(); u.setUsername("ann"); me.setUser(u);
        when(auth.authenticateTrainer(any())).thenReturn(me);
        when(trainingDao.listForTrainer(anyString(), any(), any(), any(), any()))
                .thenReturn(List.of(new Training()));

        var out = svc.listForTrainer(creds("ann","p"), "ann", new TrainingCriteria());
        assertEquals(1, out.size());
        assertEquals(1L, reg.find("gymcrm.trainings.list").tags("side","trainer").timer().count());
    }

    private Credentials creds(String u, String p) { var c = new Credentials(); c.setUsername(u); c.setPassword(p); return c; }
}
