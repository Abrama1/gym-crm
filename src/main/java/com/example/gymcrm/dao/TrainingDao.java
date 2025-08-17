package com.example.gymcrm.dao;

import com.example.gymcrm.domain.Training;
import java.util.Collection;
import java.util.Optional;

public interface TrainingDao {
    Optional<Training> findById(Long id);
    Collection<Training> findAll();
    Training save(Training training);
}
