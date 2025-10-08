package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TraineeServiceImplSetTrainersNegativeTest {

    private TraineeServiceImpl service;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private Trainee me;

    @BeforeEach
    void setup() {
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);
        var userDao = mock(UserDao.class);
        var encoder = mock(PasswordEncoder.class);

        service = new TraineeServiceImpl(traineeDao, trainerDao, userDao,
                null, null, new SimpleMeterRegistry(), encoder);

        var u = new User(); u.setUsername("me");
        me = new Trainee(); me.setUser(u); // assumes getTrainers() non-null in entity
        when(traineeDao.findByUsername("me")).thenReturn(Optional.of(me));
    }

    @Test
    void setTrainers_denies_other_username() {
        assertThrows(AuthFailedException.class,
                () -> service.setTrainers(new Credentials("me","pw"), "other", java.util.List.of()));
    }

    @Test
    void setTrainers_throws_when_trainer_missing() {
        when(trainerDao.findByUsername("t1")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.setTrainers(new Credentials("me","pw"), "me", java.util.List.of("t1")));
    }
}
