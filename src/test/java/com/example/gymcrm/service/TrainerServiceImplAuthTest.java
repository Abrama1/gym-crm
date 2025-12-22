package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplAuthTest {

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
    void getByUsername_ok() {
        Trainer tr = new Trainer();
        when(trainerDao.findByUsername("bob")).thenReturn(Optional.of(tr));

        assertSame(tr, service.getByUsername("bob"));
    }

    @Test
    void getByUsername_notFound() {
        when(trainerDao.findByUsername("bob")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getByUsername("bob"));
    }
}
