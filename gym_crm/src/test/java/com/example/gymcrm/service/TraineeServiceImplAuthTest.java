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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplAuthTest {

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
    void getByUsername_ok() {
        var t = new Trainee();
        when(traineeDao.findByUsername("alice")).thenReturn(Optional.of(t));

        var res = service.getByUsername("alice");
        assertSame(t, res);
    }

    @Test
    void getByUsername_notFound() {
        when(traineeDao.findByUsername("alice")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getByUsername("alice"));
    }
}
