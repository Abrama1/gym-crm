package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplCrudPositiveTest {
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
    void create_savesUserAndTrainer() {
        when(trainerDao.save(any(Trainer.class))).thenAnswer(inv -> { var t = inv.getArgument(0, Trainer.class); t.setId(11L); return t; });
        var saved = svc.create(new Trainer(), "Ann","Lee", true);
        assertNotNull(saved.getId());
    }

    @Test
    void update_ok() {
        var t = new Trainer(); t.setId(5L);
        when(trainerDao.findById(5L)).thenReturn(java.util.Optional.of(t));
        when(trainerDao.save(t)).thenReturn(t);
        assertSame(t, svc.update(t));
    }
}
