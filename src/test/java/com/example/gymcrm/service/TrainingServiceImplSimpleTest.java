package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainingServiceImplSimpleTest {

    @Test
    void create_increments_counter() {
        var trainingDao = mock(TrainingDao.class);
        var typeDao = mock(TrainingTypeDao.class);
        var traineeDao = mock(TraineeDao.class);
        var trainerDao = mock(TrainerDao.class);
        var meter = new SimpleMeterRegistry();

        when(typeDao.findByName("Cardio")).thenReturn(Optional.of(new TrainingType()));
        when(traineeDao.findById(1L)).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findById(2L)).thenReturn(Optional.of(new Trainer()));
        when(trainingDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var svc = new TrainingServiceImpl(trainingDao, typeDao, traineeDao, trainerDao, meter);

        var tr = new Training();
        tr.setTrainingType(new TrainingType()); tr.getTrainingType().setName("Cardio");
        tr.setTrainee(new Trainee()); tr.getTrainee().setId(1L);
        tr.setTrainer(new Trainer()); tr.getTrainer().setId(2L);

        svc.create(tr);

        assertEquals(1.0, meter.counter("gymcrm.trainings.created").count(), 0.0001);
    }
}
