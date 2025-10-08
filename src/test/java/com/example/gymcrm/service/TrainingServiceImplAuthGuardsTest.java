package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class TrainingServiceImplAuthGuardsTest {

    private TrainingServiceImpl svc;

    @BeforeEach
    void setup() {
        svc = new TrainingServiceImpl(
                mock(TrainingDao.class),
                mock(TrainingTypeDao.class),
                mock(TraineeDao.class),
                mock(TrainerDao.class),
                new SimpleMeterRegistry());
    }

    @Test
    void trainee_list_denies_other_username() {
        var creds = new com.example.gymcrm.dto.Credentials("me","pw");
        assertThrows(AuthFailedException.class,
                () -> svc.listForTrainee(creds, "someoneelse", new TrainingCriteria()));
    }

    @Test
    void trainer_list_denies_other_username() {
        var creds = new com.example.gymcrm.dto.Credentials("trainer1","pw");
        assertThrows(AuthFailedException.class,
                () -> svc.listForTrainer(creds, "other", new TrainingCriteria()));
    }
}
