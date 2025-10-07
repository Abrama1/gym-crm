package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplCreateValidationTest {
    private TrainingDao trainingDao;
    private TrainingTypeDao typeDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingServiceImpl svc;

    @BeforeEach
    void setUp() {
        trainingDao = mock(TrainingDao.class);
        typeDao = mock(TrainingTypeDao.class);
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);
        svc = new TrainingServiceImpl(trainingDao, typeDao, traineeDao, trainerDao,
                mock(AuthService.class), new SimpleMeterRegistry());
    }

    @Test
    void create_missingType_throws() {
        var t = new Training(); t.setTrainingType(new TrainingType()); t.getTrainingType().setName("X");
        t.setTrainee(new Trainee()); t.getTrainee().setId(1L);
        t.setTrainer(new Trainer()); t.getTrainer().setId(2L);
        when(typeDao.findByName("X")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> svc.create(t));
    }

    @Test
    void create_success_persistsAndCounts() {
        var tt = new TrainingType(); tt.setName("Cardio");
        var trainee = new Trainee(); trainee.setId(1L);
        var trainer = new Trainer(); trainer.setId(2L);

        when(typeDao.findByName("Cardio")).thenReturn(Optional.of(tt));
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerDao.findById(2L)).thenReturn(Optional.of(trainer));
        when(trainingDao.save(any(Training.class))).thenAnswer(inv -> { var t = inv.getArgument(0, Training.class); t.setId(10L); return t; });

        var t = new Training();
        t.setTrainingType(tt);
        t.setTrainee(trainee);
        t.setTrainer(trainer);
        t.setTrainingName("Run");
        t.setTrainingDate(LocalDateTime.now());
        t.setDurationMinutes(30);

        assertNotNull(svc.create(t).getId());
    }
}
