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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class TraineeServiceImplCrudPositiveTest {

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
                traineeDao,
                trainerDao,
                userDao,
                usernameGenerator,
                passwordGenerator,
                new SimpleMeterRegistry(),
                passwordEncoder
        );
    }

    @Test
    void create_ok() {
        when(usernameGenerator.generateUnique("John", "Doe")).thenReturn("john.doe1");
        when(passwordGenerator.random10()).thenReturn("Plain#123");
        when(passwordEncoder.encode("Plain#123")).thenReturn("HASH");

        Trainee t = new Trainee();
        when(traineeDao.save(any())).thenAnswer(invocation -> {
            Trainee saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        Trainee out = service.create(t, "John", "Doe", true);

        verify(userDao).save(any(User.class));
        assertSame(t, out);
    }
}
