package com.example.gymcrm.dao;

import com.example.gymcrm.entity.Trainer;
import java.util.*;

public interface TrainerDao {
    Optional<Trainer> findById(Long id);
    Optional<Trainer> findByUsername(String username);
    Optional<Trainer> findByUserId(Long userId);
    Collection<Trainer> findAll();
    Trainer save(Trainer t);
    Collection<Trainer> listNotAssignedToTrainee(String traineeUsername);
}
