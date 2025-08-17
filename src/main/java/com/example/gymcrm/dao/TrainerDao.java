package com.example.gymcrm.dao;

import com.example.gymcrm.domain.Trainer;
import java.util.Collection;
import java.util.Optional;

public interface TrainerDao {
    Optional<Trainer> findById(Long id);
    Collection<Trainer> findAll();
    Trainer save(Trainer trainer);
}
