package com.example.gymcrm.facade;

import com.example.gymcrm.domain.*;
import com.example.gymcrm.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GymFacadeTest {

    @Mock TraineeService traineeService;
    @Mock TrainerService trainerService;
    @Mock TrainingService trainingService;

    private GymFacade facade;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        facade = new GymFacade(traineeService, trainerService, trainingService);
    }

    @Test
    void createTraineeProfile_delegates() {
        Trainee input = new Trainee();
        Trainee out = new Trainee(); out.setId(1L);
        when(traineeService.create(any(), any(), any(), anyBoolean())).thenReturn(out);

        Trainee res = facade.createTraineeProfile(input, "A","B", true);
        assertSame(out, res);
        verify(traineeService).create(input, "A","B", true);
    }

    @Test
    void createTrainerProfile_delegates() {
        Trainer in = new Trainer(); Trainer out = new Trainer();
        when(trainerService.create(any(), any(), any(), anyBoolean())).thenReturn(out);

        Trainer res = facade.createTrainerProfile(in, "X","Y", true);
        assertSame(out, res);
        verify(trainerService).create(in, "X","Y", true);
    }

    @Test
    void createTraining_delegates() {
        Training in = new Training(); Training out = new Training();
        when(trainingService.create(any())).thenReturn(out);

        Training res = facade.createTraining(in);
        assertSame(out, res);
        verify(trainingService).create(in);
    }
}
