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
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplCrudPositiveTest {

    private TrainerServiceImpl service;
    private TrainerDao trainerDao;
    private UserDao userDao;
    private UsernameGenerator usernameGen;
    private PasswordGenerator passwordGen;
    private PasswordEncoder encoder;

    @BeforeEach
    void setup() {
        trainerDao = mock(TrainerDao.class);
        userDao = mock(UserDao.class);
        usernameGen = mock(UsernameGenerator.class);
        passwordGen = mock(PasswordGenerator.class);
        encoder = mock(PasswordEncoder.class);

        service = new TrainerServiceImpl(trainerDao, userDao,
                usernameGen, passwordGen, new SimpleMeterRegistry(), encoder);
    }

    @Test
    void create_hashes_and_sets_plainPassword() {
        when(usernameGen.generateUnique("Jane","Doe")).thenReturn("jane.doe");
        when(passwordGen.random10()).thenReturn("RAW-7777");
        when(encoder.encode("RAW-7777")).thenReturn("$bcrypt$trainer");
        when(trainerDao.save(any())).thenAnswer(inv -> {
            Trainer t = inv.getArgument(0);
            t.setId(99L);
            return t;
        });

        Trainer saved = service.create(new Trainer(), "Jane", "Doe", true);

        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(cap.capture());
        User u = cap.getValue();
        assertEquals("jane.doe", u.getUsername());
        assertEquals("$bcrypt$trainer", u.getPassword());
        assertEquals("RAW-7777", u.getPlainPassword());
        assertNotNull(saved.getId());
    }

    @Test
    void changePassword_hashes() {
        var me = new Trainer();
        var u = new User(); u.setUsername("trainer1"); me.setUser(u);
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(me));
        when(encoder.encode("N3w")).thenReturn("$n3w");

        service.changePassword(new com.example.gymcrm.dto.Credentials("trainer1","x"), "N3w");

        assertEquals("$n3w", u.getPassword());
        verify(userDao).save(u);
    }
}
