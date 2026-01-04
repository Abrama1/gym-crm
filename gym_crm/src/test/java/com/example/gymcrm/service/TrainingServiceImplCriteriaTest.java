package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplCriteriaTest {

    private TrainingDao trainingDao;
    private TrainingTypeDao trainingTypeDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingServiceImpl service;

    @BeforeEach
    void setUp() {
        trainingDao = mock(TrainingDao.class);
        trainingTypeDao = mock(TrainingTypeDao.class);
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);

        service = new TrainingServiceImpl(
                trainingDao, trainingTypeDao, traineeDao, trainerDao,
                new SimpleMeterRegistry()
        );
    }

    @Test
    void listForTrainee_lowercasesOtherPartyName() {
        when(trainingDao.listForTrainee(anyString(), any(), any(), any(), any()))
                .thenReturn(List.of());

        TrainingCriteria c = new TrainingCriteria(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                "Yoga",
                "JOHN DOE"
        );

        service.listForTrainee("alice", c);

        verify(trainingDao).listForTrainee(
                eq("alice"),
                any(), any(),
                eq("john doe"),
                eq("Yoga")
        );
    }

    @Test
    void listForTrainer_lowercasesOtherPartyName() {
        when(trainingDao.listForTrainer(anyString(), any(), any(), any(), any()))
                .thenReturn(List.of());

        TrainingCriteria c = new TrainingCriteria(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                "Box",
                "ANN SMITH"
        );

        service.listForTrainer("bob", c);

        verify(trainingDao).listForTrainer(
                eq("bob"),
                any(), any(),
                eq("ann smith"),
                eq("Box")
        );
    }
}
