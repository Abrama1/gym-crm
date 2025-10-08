package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplAuthTest {

    private TraineeServiceImpl service;
    private TraineeDao traineeDao;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        var trainerDao = mock(TrainerDao.class);
        var userDao = mock(UserDao.class);
        var encoder = mock(PasswordEncoder.class);

        service = new TraineeServiceImpl(
                traineeDao, trainerDao, userDao,
                null, null, new SimpleMeterRegistry(), encoder);
    }

    @Test
    void getByUsername_denies_otherUser() {
        assertThrows(AuthFailedException.class,
                () -> service.getByUsername(new Credentials("me", "pw"), "someoneelse"));
    }

    @Test
    void getByUsername_ok_self() {
        var meU = new User(); meU.setUsername("me");
        var me = new Trainee(); me.setUser(meU);

        when(traineeDao.findByUsername("me")).thenReturn(Optional.of(me));
        var res = service.getByUsername(new Credentials("me", "pw"), "me");
        assertSame(me, res);
    }
}
