package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplCriteriaTest {

    @Test
    void passes_criteria_to_dao_for_trainee() {
        var trainingDao = mock(TrainingDao.class);
        var svc = new TrainingServiceImpl(trainingDao, mock(TrainingTypeDao.class),
                mock(TraineeDao.class), mock(TrainerDao.class), new SimpleMeterRegistry());

        var c = new TrainingCriteria(LocalDateTime.now().minusDays(1), LocalDateTime.now(),
                "Cardio", "Jane");

        svc.listForTrainee(new com.example.gymcrm.dto.Credentials("me","x"), "me", c);

        verify(trainingDao).listForTrainee(eq("me"), any(), any(), eq("jane"), eq("Cardio"));
    }

    @Test
    void passes_criteria_to_dao_for_trainer() {
        var trainingDao = mock(TrainingDao.class);

        var svc = new TrainingServiceImpl(trainingDao, mock(TrainingTypeDao.class),
                mock(TraineeDao.class), mock(TrainerDao.class), new SimpleMeterRegistry());

        var c = new TrainingCriteria(LocalDateTime.now().minusDays(7), LocalDateTime.now(),
                "Stretch", "John");

        svc.listForTrainer(new com.example.gymcrm.dto.Credentials("t1","x"), "t1", c);

        verify(trainingDao).listForTrainer(eq("t1"), any(), any(), eq("john"), eq("Stretch"));
    }
}
