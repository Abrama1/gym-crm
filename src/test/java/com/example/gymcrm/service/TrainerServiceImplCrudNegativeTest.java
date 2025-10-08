package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TrainerServiceImplCrudNegativeTest {

    private TrainerDao trainerDao;
    private TrainerServiceImpl service;

    @BeforeEach
    void setup() {
        trainerDao = mock(TrainerDao.class);
        var userDao = mock(UserDao.class);
        var encoder = mock(PasswordEncoder.class);
        service = new TrainerServiceImpl(trainerDao, userDao,
                null, null, new SimpleMeterRegistry(), encoder);
    }

    @Test
    void update_notFound() {
        var t = new Trainer(); t.setId(7L);
        when(trainerDao.findById(7L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.update(t));
    }
}
