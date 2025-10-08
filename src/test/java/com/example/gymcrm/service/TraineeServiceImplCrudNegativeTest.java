package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TraineeServiceImplCrudNegativeTest {

    private TraineeDao traineeDao;
    private TraineeServiceImpl service;

    @BeforeEach
    void setup() {
        traineeDao = mock(TraineeDao.class);
        var trainerDao = mock(TrainerDao.class);
        var userDao = mock(UserDao.class);
        var encoder = mock(PasswordEncoder.class);

        service = new TraineeServiceImpl(traineeDao, trainerDao, userDao,
                null, null, new SimpleMeterRegistry(), encoder);
    }

    @Test
    void update_throws_when_notFound() {
        var t = new Trainee(); t.setId(99L);
        when(traineeDao.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.update(t));
    }

    @Test
    void delete_throws_when_notFound() {
        when(traineeDao.findById(42L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.delete(42L));
    }
}
