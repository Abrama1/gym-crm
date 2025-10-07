package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplListNotAssignedPositiveTest {
    private TrainerServiceImpl svc;
    private TrainerDao trainerDao;
    private AuthService auth;

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        auth = mock(AuthService.class);
        svc = new TrainerServiceImpl(trainerDao, mock(UserDao.class),
                mock(UsernameGenerator.class), mock(PasswordGenerator.class),
                auth, new SimpleMeterRegistry());
    }

    @Test
    void listNotAssigned_authIsInvoked_andReturnsList() {
        when(trainerDao.listNotAssignedToTrainee("trainee.1")).thenReturn(List.of(new Trainer()));

        var out = svc.listNotAssignedToTrainee(creds("tr","pw"), "trainee.1");
        assertEquals(1, out.size());
        verify(auth).authenticateTrainer(any());
    }

    private com.example.gymcrm.dto.Credentials creds(String u, String p) {
        var c = new com.example.gymcrm.dto.Credentials(); c.setUsername(u); c.setPassword(p); return c;
    }
}
