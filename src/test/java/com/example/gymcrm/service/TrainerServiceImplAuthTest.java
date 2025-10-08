package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplAuthTest {

    private TrainerServiceImpl service;
    private TrainerDao trainerDao;

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        var userDao = mock(UserDao.class);
        var encoder = mock(PasswordEncoder.class);
        service = new TrainerServiceImpl(trainerDao, userDao, null, null, new SimpleMeterRegistry(), encoder);
    }

    @Test
    void getByUsername_denies_other() {
        assertThrows(AuthFailedException.class,
                () -> service.getByUsername(new Credentials("trainer1","pw"), "another"));
    }

    @Test
    void getByUsername_ok_self() {
        var meU = new User(); meU.setUsername("trainer1");
        var me = new Trainer(); me.setUser(meU);

        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(me));
        assertSame(me, service.getByUsername(new Credentials("trainer1","pw"), "trainer1"));
    }
}
