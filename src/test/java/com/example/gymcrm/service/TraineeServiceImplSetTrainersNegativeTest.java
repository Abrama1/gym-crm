package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TraineeServiceImplSetTrainersNegativeTest {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private UserDao userDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private PasswordEncoder passwordEncoder;
    private TraineeServiceImpl service;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);
        userDao = mock(UserDao.class);
        usernameGenerator = mock(UsernameGenerator.class);
        passwordGenerator = mock(PasswordGenerator.class);
        passwordEncoder = mock(PasswordEncoder.class);

        service = new TraineeServiceImpl(
                traineeDao, trainerDao, userDao,
                usernameGenerator, passwordGenerator,
                new SimpleMeterRegistry(), passwordEncoder
        );
    }

    @Test
    void setTrainers_traineeNotFound_throws() {
        when(traineeDao.findByUsername("alice")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.setTrainers("alice", List.of("t1")));
    }

    @Test
    void setTrainers_trainerNotFound_throws() {
        Trainee me = new Trainee();
        when(traineeDao.findByUsername("alice")).thenReturn(Optional.of(me));
        when(trainerDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.setTrainers("alice", List.of("missing")));
    }
}
