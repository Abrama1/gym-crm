package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TrainingServiceImplSimpleTest {

    @Mock TrainingDao trainingDao;
    @Mock TrainingTypeDao trainingTypeDao;
    @Mock TraineeDao traineeDao;
    @Mock TrainerDao trainerDao;
    @Mock AuthService authService;

    TrainingService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new TrainingServiceImpl(trainingDao, trainingTypeDao, traineeDao, trainerDao, authService);
    }

    @Test
    void getById_and_list() {
        Training t = new Training(); t.setId(3L);
        when(trainingDao.findById(3L)).thenReturn(Optional.of(t));
        when(trainingDao.findAll()).thenReturn(List.of(t));
        assertTrue(service.getById(3L).isPresent());
        assertEquals(1, service.list().size());
    }
}
