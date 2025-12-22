package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class TrainerServiceImplCrudPositiveTest {

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
        when(usernameGenerator.generateUnique("Ann", "Lee")).thenReturn("ann.lee1");
        when(passwordGenerator.random10()).thenReturn("Plain#456");
        when(passwordEncoder.encode("Plain#456")).thenReturn("HASH");

        Trainer tr = new Trainer();
        when(trainerDao.save(any())).thenAnswer(invocation -> {
            Trainer saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        Trainer out = service.create(tr, "Ann", "Lee", true);

        verify(userDao).save(any(User.class));
        assertSame(tr, out);
    }
}
