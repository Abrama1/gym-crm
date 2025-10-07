package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplAuthTest {
    private TrainerServiceImpl svc;
    private AuthService auth;

    @BeforeEach
    void setUp() {
        svc = new TrainerServiceImpl(mock(TrainerDao.class), mock(UserDao.class),
                mock(UsernameGenerator.class), mock(PasswordGenerator.class),
                auth = mock(AuthService.class), new SimpleMeterRegistry());
    }

    @Test
    void getByUsername_otherUser_forbidden() {
        var me = new Trainer(); var u = new User(); u.setUsername("me"); me.setUser(u);
        when(auth.authenticateTrainer(any())).thenReturn(me);

        assertThrows(AuthFailedException.class, () -> svc.getByUsername(creds("me","p"), "other"));
    }

    private com.example.gymcrm.dto.Credentials creds(String u, String p) {
        var c = new com.example.gymcrm.dto.Credentials(); c.setUsername(u); c.setPassword(p); return c;
    }
}
