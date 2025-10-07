package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplSetTrainersNegativeTest {
    private TraineeServiceImpl svc;
    private AuthService auth;
    private TrainerDao trainerDao;
    private TraineeDao traineeDao;

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        traineeDao = mock(TraineeDao.class);
        svc = new TraineeServiceImpl(traineeDao, trainerDao, mock(UserDao.class),
                mock(UsernameGenerator.class), mock(PasswordGenerator.class),
                auth = mock(AuthService.class), new SimpleMeterRegistry());
    }

    @Test
    void setTrainers_otherUser_forbidden() {
        var me = new Trainee(); var u = new User(); u.setUsername("me"); me.setUser(u);
        when(auth.authenticateTrainee(any())).thenReturn(me);

        assertThrows(AuthFailedException.class,
                () -> svc.setTrainers(creds("me","p"), "other", List.of("x")));
    }

    @Test
    void setTrainers_unknownTrainer_throwsNotFound() {
        var me = new Trainee(); var u = new User(); u.setUsername("me"); me.setUser(u);
        when(auth.authenticateTrainee(any())).thenReturn(me);
        when(trainerDao.findByUsername("x")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> svc.setTrainers(creds("me","p"), "me", List.of("x")));
    }

    private com.example.gymcrm.dto.Credentials creds(String u, String p) {
        var c = new com.example.gymcrm.dto.Credentials(); c.setUsername(u); c.setPassword(p); return c;
    }
}
