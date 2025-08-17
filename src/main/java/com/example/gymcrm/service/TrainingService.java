package com.example.gymcrm.service;

import com.example.gymcrm.domain.Training;
import java.util.List;
import java.util.Optional;

public interface TrainingService {
    Training create(Training training);
    Optional<Training> getById(Long id);
    List<Training> list();
}
