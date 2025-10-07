package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplCrudNegativeTest {
    private TrainerServiceImpl svc;
    private TrainerDao trainerDao;

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        svc = new TrainerServiceImpl(trainerDao, mock(UserDao.class),
                mock(UsernameGenerator.class), mock(PasswordGenerator.class),
                mock(AuthService.class), new SimpleMeterRegistry());
    }

    @Test
    void update_missingId_throws() {
        assertThrows(NotFoundException.class, () -> svc.update(new Trainer()));
    }

    @Test
    void update_notFound_throws() {
        var t = new Trainer(); t.setId(9L);
        when(trainerDao.findById(9L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> svc.update(t));
    }
}
