package com.example.gymcrm.service;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TrainingServiceImplCreateValidationTest {

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
    }

    @Test
    void create_missingType_throws() {
        Training x = new Training();
        TrainingType tt = new TrainingType(); tt.setName("Cardio");
        x.setTrainingType(tt);
        x.setTrainingDate(LocalDateTime.now());
        assertThrows(NotFoundException.class, () -> service.create(x));
    }

    @Test
    void create_missingTraineeOrTrainer_throws() {
        TrainingType tt = new TrainingType(); tt.setName("Cardio");
        when(trainingTypeDao.findByName("Cardio")).thenReturn(Optional.of(tt));

        Training x = new Training();
        x.setTrainingType(tt);
        x.setTrainingDate(LocalDateTime.now());

        assertThrows(NotFoundException.class, () -> service.create(x)); // no trainee/trainer
    }
}
