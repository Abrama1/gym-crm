package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TrainingServiceImplCreateValidationTest {

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
    void create_missingTypeName_throws() {
        Training tr = new Training();
        tr.setTrainingType(new TrainingType()); // name null

        assertThrows(NotFoundException.class, () -> service.create(tr));
    }

    @Test
    void create_missingTrainee_throws() {
        Training tr = new Training();
        TrainingType type = new TrainingType(); type.setName("Yoga");
        tr.setTrainingType(type);
        when(trainingTypeDao.findByName("Yoga")).thenReturn(Optional.of(new TrainingType()));

        tr.setTrainer(new Trainer());
        tr.getTrainer().setId(1L);

        assertThrows(NotFoundException.class, () -> service.create(tr));
    }

    @Test
    void create_missingTrainer_throws() {
        Training tr = new Training();
        TrainingType type = new TrainingType(); type.setName("Yoga");
        tr.setTrainingType(type);
        when(trainingTypeDao.findByName("Yoga")).thenReturn(Optional.of(new TrainingType()));

        tr.setTrainee(new Trainee());
        tr.getTrainee().setId(2L);

        assertThrows(NotFoundException.class, () -> service.create(tr));
    }
}
