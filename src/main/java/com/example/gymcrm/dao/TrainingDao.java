package com.example.gymcrm.dao;

import com.example.gymcrm.entity.Training;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface TrainingDao {
    Optional<Training> findById(Long id);
    Collection<Training> findAll();
    Training save(Training t);

    // queries with optional filters (pass nulls when not filtering)
    Collection<Training> listForTrainee(String traineeUsername,
                                        LocalDateTime from, LocalDateTime to,
                                        String trainerNameLike, String trainingType);

    Collection<Training> listForTrainer(String trainerUsername,
                                        LocalDateTime from, LocalDateTime to,
                                        String traineeNameLike, String trainingType);
}
