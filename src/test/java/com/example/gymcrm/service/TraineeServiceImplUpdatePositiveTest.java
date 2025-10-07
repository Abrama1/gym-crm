package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplUpdatePositiveTest {
    private TraineeServiceImpl svc;
    private TraineeDao traineeDao;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        svc = new TraineeServiceImpl(traineeDao, mock(TrainerDao.class), mock(UserDao.class),
                mock(UsernameGenerator.class), mock(PasswordGenerator.class),
                mock(AuthService.class), new SimpleMeterRegistry());
    }

    @Test
    void updateProfile_updatesAddressAndDob() {
        var me = new Trainee(); me.setUser(new User());
        when(((AuthService) any()).authenticateTrainee(any())).thenThrow(new RuntimeException("not used"));
    }
}
