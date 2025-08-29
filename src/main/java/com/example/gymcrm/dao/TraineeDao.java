package com.example.gymcrm.dao;

import com.example.gymcrm.entity.Trainee;
import java.util.*;

public interface TraineeDao {
    Optional<Trainee> findById(Long id);
    Optional<Trainee> findByUsername(String username);
    Optional<Trainee> findByUserId(Long userId);
    Collection<Trainee> findAll();
    Trainee save(Trainee t);
    void deleteById(Long id);
    void delete(Trainee t);
}
