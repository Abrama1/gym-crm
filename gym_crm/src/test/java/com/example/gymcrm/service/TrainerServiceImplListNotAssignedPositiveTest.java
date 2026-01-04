package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainerServiceImplListNotAssignedPositiveTest {

    private TrainerDao trainerDao;
    private UserDao userDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private PasswordEncoder passwordEncoder;
    private TrainerServiceImpl service;

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        userDao = mock(UserDao.class);
        usernameGenerator = mock(UsernameGenerator.class);
        passwordGenerator = mock(PasswordGenerator.class);
        passwordEncoder = mock(PasswordEncoder.class);

        service = new TrainerServiceImpl(
                trainerDao, userDao,
                usernameGenerator, passwordGenerator,
                new SimpleMeterRegistry(), passwordEncoder
        );
    }

    @Test
    void listNotAssigned_ok() {
        when(trainerDao.listNotAssignedToTrainee("alice"))
                .thenReturn(List.of(new Trainer(), new Trainer(), new Trainer()));

        var out = service.listNotAssignedToTrainee("alice");
        assertEquals(3, out.size());
    }
}
