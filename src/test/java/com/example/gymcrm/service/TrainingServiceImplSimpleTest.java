package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplSimpleTest {
    private TrainingDao trainingDao;
    private TrainingServiceImpl svc;

    @BeforeEach
    void setUp() {
        trainingDao = mock(TrainingDao.class);
        svc = new TrainingServiceImpl(trainingDao, mock(TrainingTypeDao.class),
                mock(TraineeDao.class), mock(TrainerDao.class),
                mock(AuthService.class), new SimpleMeterRegistry());
    }

    @Test
    void getById_and_list() {
        var t = new Training(); t.setId(7L);
        when(trainingDao.findById(7L)).thenReturn(Optional.of(t));
        when(trainingDao.findAll()).thenReturn(List.of(t));

        assertTrue(svc.getById(7L).isPresent());
        assertEquals(1, svc.list().size());
    }
}
