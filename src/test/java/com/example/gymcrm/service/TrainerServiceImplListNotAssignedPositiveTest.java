package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainerServiceImplListNotAssignedPositiveTest {

    @Test
    void lists_after_principal_check() {
        var trainerDao = mock(TrainerDao.class);
        var userDao = mock(com.example.gymcrm.dao.UserDao.class);
        var encoder = mock(PasswordEncoder.class);

        var svc = new TrainerServiceImpl(trainerDao, userDao, null, null, new SimpleMeterRegistry(), encoder);

        var meU = new User(); meU.setUsername("t1");
        var me = new Trainer(); me.setUser(meU);
        when(trainerDao.findByUsername("t1")).thenReturn(Optional.of(me));
        when(trainerDao.listNotAssignedToTrainee("trainee.1"))
                .thenReturn(List.of(new Trainer(), new Trainer()));

        var res = svc.listNotAssignedToTrainee(new Credentials("t1","x"), "trainee.1");
        assertEquals(2, res.size());
        verify(trainerDao).findByUsername("t1");
    }
}
