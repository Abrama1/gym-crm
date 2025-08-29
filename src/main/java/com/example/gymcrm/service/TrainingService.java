package com.example.gymcrm.service;

import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.entity.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingService {
    Training create(Training training);
    Optional<Training> getById(Long id);
    List<Training> list();

    List<Training> listForTrainee(Credentials auth, String traineeUsername, TrainingCriteria c);
    List<Training> listForTrainer(Credentials auth, String trainerUsername, TrainingCriteria c);
}
