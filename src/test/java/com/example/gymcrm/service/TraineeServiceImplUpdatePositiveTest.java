package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TraineeServiceImplUpdatePositiveTest {

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
    void updateProfile_ok() {
        User u = new User();
        u.setUsername("alice");

        Trainee me = new Trainee();
        me.setUser(u);

        when(traineeDao.findByUsername("alice")).thenReturn(Optional.of(me));
        when(traineeDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee patch = new Trainee();
        patch.setAddress("New Address");
        patch.setDateOfBirth(LocalDate.of(2000, 1, 1));

        Trainee out = service.updateProfile("alice", patch);

        assertEquals("New Address", out.getAddress());
        assertEquals(LocalDate.of(2000, 1, 1), out.getDateOfBirth());
    }
}
