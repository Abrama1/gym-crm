package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TraineeServiceImplUpdatePositiveTest {

    @Test
    void updateProfile_updates_fields() {
        var traineeDao = mock(TraineeDao.class);
        var trainerDao = mock(TrainerDao.class);
        var userDao = mock(UserDao.class);
        var encoder = mock(PasswordEncoder.class);

        var me = new Trainee(); me.setUser(new User());
        when(traineeDao.findByUsername("me")).thenReturn(java.util.Optional.of(me));
        when(traineeDao.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        var service = new TraineeServiceImpl(traineeDao, trainerDao, userDao,
                null, null, new SimpleMeterRegistry(), encoder);

        var patch = new Trainee();
        patch.setAddress("New Addr");
        patch.setDateOfBirth(LocalDate.of(2000,5,5));

        var saved = service.updateProfile(new com.example.gymcrm.dto.Credentials("me","x"), patch);

        assertEquals("New Addr", saved.getAddress());
        assertEquals(LocalDate.of(2000,5,5), saved.getDateOfBirth());
    }
}
