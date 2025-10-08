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
    private TrainingTypeDao typeDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingServiceImpl svc;

    @BeforeEach
    void setup() {
        trainingDao = mock(TrainingDao.class);
        typeDao = mock(TrainingTypeDao.class);
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);

        svc = new TrainingServiceImpl(trainingDao, typeDao, traineeDao, trainerDao,
                new SimpleMeterRegistry());
    }

    @Test
    void create_throws_when_type_missing() {
        var tr = new Training();
        tr.setTrainingType(new TrainingType()); tr.getTrainingType().setName("Yoga");
        tr.setTrainee(new Trainee()); tr.getTrainee().setId(1L);
        tr.setTrainer(new Trainer()); tr.getTrainer().setId(2L);

        when(typeDao.findByName("Yoga")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> svc.create(tr));
    }

    @Test
    void create_throws_when_refs_missing() {
        var tr = new Training();
        // no ids set for trainee/trainer
        assertThrows(NotFoundException.class, () -> svc.create(tr));
    }
}
