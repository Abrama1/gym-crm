package com.example.gymcrm.facade;

import com.example.gymcrm.entity.*;
import com.example.gymcrm.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class GymFacadeTest {

    @Mock TraineeService traineeService;
    @Mock TrainerService trainerService;
    @Mock TrainingService trainingService;

    GymFacade facade;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        facade = new GymFacade(traineeService, trainerService, trainingService);
    }

    @Test
    void delegates_to_services() {
        Trainee trn = new Trainee(); Trainee trnSaved = new Trainee();
        when(traineeService.create(trn, "John","Smith", true)).thenReturn(trnSaved);
        assertSame(trnSaved, facade.createTraineeProfile(trn, "John","Smith", true));

        Trainer r = new Trainer(); Trainer rSaved = new Trainer();
        when(trainerService.create(r, "Jane","Doe", true)).thenReturn(rSaved);
        assertSame(rSaved, facade.createTrainerProfile(r, "Jane","Doe", true));

        Training t = new Training(); Training tSaved = new Training();
        when(trainingService.create(t)).thenReturn(tSaved);
        assertSame(tSaved, facade.createTraining(t));

        verify(traineeService).create(trn, "John","Smith", true);
        verify(trainerService).create(r, "Jane","Doe", true);
        verify(trainingService).create(t);
    }
}
