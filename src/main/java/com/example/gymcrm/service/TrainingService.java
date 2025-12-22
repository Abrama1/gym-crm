package com.example.gymcrm.service;

import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.entity.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingService {
    Training create(Training training);

    Optional<Training> getById(Long id);
    List<Training> list();

    List<Training> listForTrainee(String traineeUsername, TrainingCriteria criteria);
    List<Training> listForTrainer(String trainerUsername, TrainingCriteria criteria);
}
