package com.example.gymcrm.dao;

import com.example.gymcrm.domain.Trainee;
import java.util.Collection;
import java.util.Optional;

public interface TraineeDao {
    Optional<Trainee> findById(Long id);
    Collection<Trainee> findAll();
    Trainee save(Trainee trainee);
    void deleteById(Long id);
}
