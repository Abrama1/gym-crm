package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class TrainingServiceImplSimpleTest {

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
                trainingDao,
                trainingTypeDao,
                traineeDao,
                trainerDao,
                new SimpleMeterRegistry()
        );
    }

    @Test
    void create_happyPath() {
        Training tr = new Training();

        TrainingType type = new TrainingType();
        type.setName("Yoga");
        tr.setTrainingType(type);

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        tr.setTrainee(trainee);

        Trainer trainer = new Trainer();
        trainer.setId(2L);
        tr.setTrainer(trainer);

        when(trainingTypeDao.findByName("Yoga")).thenReturn(Optional.of(new TrainingType()));
        when(traineeDao.findById(1L)).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findById(2L)).thenReturn(Optional.of(new Trainer()));
        when(trainingDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Training out = service.create(tr);
        assertSame(tr, out);
    }

    @Test
    void list_delegatesToDao() {
        when(trainingDao.findAll()).thenReturn(List.of(new Training(), new Training()));

        var list = service.list();

        verify(trainingDao).findAll();
        assertSame(2, list.size());
    }
}
