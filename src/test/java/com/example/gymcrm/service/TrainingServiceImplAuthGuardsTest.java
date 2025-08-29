package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TrainingServiceImplAuthGuardsTest {

    @Mock TrainingDao trainingDao;
    @Mock TrainingTypeDao trainingTypeDao;
    @Mock TraineeDao traineeDao;
    @Mock TrainerDao trainerDao;
    @Mock AuthService authService;

    TrainingService service;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        service = new TrainingServiceImpl(trainingDao, trainingTypeDao, traineeDao, trainerDao, authService);

        User u1 = new User(); u1.setUsername("John.Smith"); u1.setActive(true); u1.setPassword("pw");
        User u2 = new User(); u2.setUsername("Jane.Doe");   u2.setActive(true); u2.setPassword("pw");
        Trainee meT = new Trainee(); meT.setUser(u1);
        Trainer meR = new Trainer(); meR.setUser(u2);

        when(authService.authenticateTrainee(new Credentials("John.Smith","pw"))).thenReturn(meT);
        when(authService.authenticateTrainer(new Credentials("Jane.Doe","pw"))).thenReturn(meR);
    }

    @Test
    void listForTrainee_deniesOtherUser() {
        var c = new TrainingCriteria(LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null);
        assertThrows(AuthFailedException.class, () ->
                service.listForTrainee(new Credentials("John.Smith","pw"), "Other.User", c));
        verifyNoInteractions(trainingDao);
    }

    @Test
    void listForTrainer_deniesOtherUser() {
        var c = new TrainingCriteria(LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null);
        assertThrows(AuthFailedException.class, () ->
                service.listForTrainer(new Credentials("Jane.Doe","pw"), "Other.User", c));
        verifyNoInteractions(trainingDao);
    }
}
