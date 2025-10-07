package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplCrudNegativeTest {
    private TraineeServiceImpl svc;
    private TraineeDao traineeDao;
    private AuthService auth;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        svc = new TraineeServiceImpl(traineeDao, mock(TrainerDao.class), mock(UserDao.class),
                mock(UsernameGenerator.class), mock(PasswordGenerator.class),
                auth = mock(AuthService.class), new SimpleMeterRegistry());
    }

    @Test
    void update_missingId_throwsNotFound() {
        assertThrows(NotFoundException.class, () -> svc.update(new Trainee()));
    }

    @Test
    void delete_notFound_throws() {
        when(traineeDao.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> svc.delete(99L));
    }

    @Test
    void deleteByUsername_notSelf_forbidden() {
        var me = new com.example.gymcrm.entity.Trainee();
        var u = new com.example.gymcrm.entity.User(); u.setUsername("self"); me.setUser(u);
        when(auth.authenticateTrainee(any())).thenReturn(me);

        assertThrows(AuthFailedException.class,
                () -> svc.deleteByUsername(creds("self","p"), "other"));
    }

    private com.example.gymcrm.dto.Credentials creds(String u, String p) {
        var c = new com.example.gymcrm.dto.Credentials(); c.setUsername(u); c.setPassword(p); return c;
    }
}
