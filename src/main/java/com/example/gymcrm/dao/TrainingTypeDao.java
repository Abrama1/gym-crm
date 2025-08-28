package com.example.gymcrm.dao;

import com.example.gymcrm.entity.TrainingType;
import java.util.Collection;
import java.util.Optional;

public interface TrainingTypeDao {
    Optional<TrainingType> findByName(String name);
    Collection<TrainingType> findAll();
    TrainingType save(TrainingType trainingType);
}
