package com.example.gymcrm.service;

import com.example.gymcrm.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    Trainer create(Trainer trainer, String firstName, String lastName, boolean active);
    Trainer update(Trainer trainer);

    Optional<Trainer> getById(Long id);
    List<Trainer> list();

    Trainer getByUsername(String username);
    void changePassword(String username, String newPassword);
    Trainer updateProfile(String username, Trainer updates);
    void activate(String username);
    void deactivate(String username);
    List<Trainer> listNotAssignedToTrainee(String traineeUsername);
}
