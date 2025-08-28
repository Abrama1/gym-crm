package com.example.gymcrm.service;

import com.example.gymcrm.entity.Trainer;
import java.util.List;
import java.util.Optional;

public interface TrainerService {
    Trainer create(Trainer trainer, String firstName, String lastName, boolean active);
    Trainer update(Trainer trainer);
    Optional<Trainer> getById(Long trainerId);
    List<Trainer> list();
}
