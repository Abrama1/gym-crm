package com.example.gymcrm.facade;

import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainee createTraineeProfile(Trainee t, String first, String last, boolean active) {
        return traineeService.create(t, first, last, active);
    }

    public Trainer createTrainerProfile(Trainer t, String first, String last, boolean active) {
        return trainerService.create(t, first, last, active);
    }

    public Training createTraining(Training tr) {
        return trainingService.create(tr);
    }
}
