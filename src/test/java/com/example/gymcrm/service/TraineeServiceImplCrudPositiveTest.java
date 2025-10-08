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
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplCrudPositiveTest {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private UserDao userDao;
    private UsernameGenerator usernameGen;
    private PasswordGenerator passwordGen;
    private PasswordEncoder encoder;
    private SimpleMeterRegistry meter;
    private TraineeServiceImpl service;

    @BeforeEach
    void setup() {
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);
        userDao = mock(UserDao.class);
        usernameGen = mock(UsernameGenerator.class);
        passwordGen = mock(PasswordGenerator.class);
        encoder = mock(PasswordEncoder.class);
        meter = new SimpleMeterRegistry();

        service = new TraineeServiceImpl(traineeDao, trainerDao, userDao,
                usernameGen, passwordGen, meter, encoder);
    }

    @Test
    void create_hashes_and_sets_plainPassword() {
        when(usernameGen.generateUnique("John","Smith")).thenReturn("john.smith");
        when(passwordGen.random10()).thenReturn("RAWp@ss123");
        when(encoder.encode("RAWp@ss123")).thenReturn("$bcrypt$hash");
        when(traineeDao.save(any())).thenAnswer(inv -> {
            Trainee t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        Trainee saved = service.create(new Trainee(), "John", "Smith", true);

        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(cap.capture());
        User u = cap.getValue();
        assertEquals("john.smith", u.getUsername());
        assertEquals("$bcrypt$hash", u.getPassword());
        assertEquals("RAWp@ss123", u.getPlainPassword()); // returned once
        assertNotNull(saved.getId());
    }

    @Test
    void changePassword_hashes_and_saves() {
        var u = new User(); u.setUsername("me");
        var me = new Trainee(); me.setUser(u);

        when(traineeDao.findByUsername("me")).thenReturn(java.util.Optional.of(me));
        when(encoder.encode("NewPass!")).thenReturn("$newHash");

        service.changePassword(new com.example.gymcrm.dto.Credentials("me","x"), "NewPass!");

        assertEquals("$newHash", u.getPassword());
        verify(userDao).save(u);
    }
}
