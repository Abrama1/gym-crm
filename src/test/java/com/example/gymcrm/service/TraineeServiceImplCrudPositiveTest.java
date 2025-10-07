package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplCrudPositiveTest {
    private TraineeServiceImpl svc;
    private TraineeDao traineeDao;
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        userDao = mock(UserDao.class);
        svc = new TraineeServiceImpl(traineeDao, mock(TrainerDao.class), userDao,
                mock(UsernameGenerator.class), mock(PasswordGenerator.class),
                mock(AuthService.class), new SimpleMeterRegistry());
    }

    @Test
    void create_savesUserAndTrainee() {
        when(traineeDao.save(any(Trainee.class))).thenAnswer(inv -> { var t = inv.getArgument(0, Trainee.class); t.setId(1L); return t; });

        var saved = svc.create(new Trainee(), "John","Smith", true);

        assertNotNull(saved.getId());
        verify(userDao).save(any(User.class));
        verify(traineeDao).save(any(Trainee.class));
    }

    @Test
    void update_succeeds_whenExists() {
        var t = new Trainee(); t.setId(5L);
        when(traineeDao.findById(5L)).thenReturn(java.util.Optional.of(t));
        when(traineeDao.save(t)).thenReturn(t);

        assertSame(t, svc.update(t));
    }
}
