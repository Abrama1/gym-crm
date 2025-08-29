package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.dto.*;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainingServiceImplCriteriaTest {

    @Mock TrainingDao trainingDao;
    @Mock TrainingTypeDao trainingTypeDao;
    @Mock TraineeDao traineeDao;
    @Mock TrainerDao trainerDao;
    @Mock AuthService authService;

    TrainingService service;

    private Trainee meTrainee;
    private Trainer meTrainer;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        service = new TrainingServiceImpl(trainingDao, trainingTypeDao, traineeDao, trainerDao, authService);

        User u1 = new User(); u1.setId(1L); u1.setUsername("John.Smith"); u1.setActive(true); u1.setPassword("pw");
        User u2 = new User(); u2.setId(2L); u2.setUsername("Jane.Doe"); u2.setActive(true); u2.setPassword("pw");
        meTrainee = new Trainee(); meTrainee.setUser(u1);
        meTrainer = new Trainer(); meTrainer.setUser(u2);

        when(authService.authenticateTrainee(new Credentials("John.Smith","pw"))).thenReturn(meTrainee);
        when(authService.authenticateTrainer(new Credentials("Jane.Doe","pw"))).thenReturn(meTrainer);
    }

    @Test
    void listForTrainee_forwardsCriteria() {
        Credentials c = new Credentials("John.Smith","pw");
        TrainingCriteria tc = new TrainingCriteria(LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(1), "Cardio", "%jane doe%");
        when(trainingDao.listForTrainee(any(), any(), any(), any(), any())).thenReturn(List.of());
        var res = service.listForTrainee(c, "John.Smith", tc);
        assertEquals(0, res.size());
        verify(trainingDao).listForTrainee(eq("John.Smith"), eq(tc.getFrom()), eq(tc.getTo()), eq(tc.getOtherPartyNameLike().toLowerCase()), eq("Cardio"));
    }

    @Test
    void listForTrainer_forwardsCriteria() {
        Credentials c = new Credentials("Jane.Doe","pw");
        TrainingCriteria tc = new TrainingCriteria(LocalDateTime.now().minusDays(3), LocalDateTime.now(), "Strength", "%john smith%");
        when(trainingDao.listForTrainer(any(), any(), any(), any(), any())).thenReturn(List.of());
        var res = service.listForTrainer(c, "Jane.Doe", tc);
        assertEquals(0, res.size());
        verify(trainingDao).listForTrainer(eq("Jane.Doe"), eq(tc.getFrom()), eq(tc.getTo()), eq(tc.getOtherPartyNameLike().toLowerCase()), eq("Strength"));
    }

    @Test
    void create_validatesTypeAndRefs() {
        TrainingType tt = new TrainingType(); tt.setName("Cardio");
        when(trainingTypeDao.findByName("Cardio")).thenReturn(Optional.of(tt));
        Trainee t = new Trainee(); t.setId(10L);
        Trainer r = new Trainer(); r.setId(20L);
        when(traineeDao.findById(10L)).thenReturn(Optional.of(t));
        when(trainerDao.findById(20L)).thenReturn(Optional.of(r));
        when(trainingDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Training x = new Training();
        x.setTrainingType(tt); x.setTrainee(t); x.setTrainer(r);
        x.setTrainingName("Run"); x.setTrainingDate(LocalDateTime.now()); x.setDurationMinutes(30);

        service.create(x);
        verify(trainingDao).save(any(Training.class));
    }
}
