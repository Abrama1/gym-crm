package com.example.gymcrm.service;

import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    Trainer create(Trainer trainer, String firstName, String lastName, boolean active);
    Trainer update(Trainer trainer);
    Optional<Trainer> getById(Long trainerId);
    List<Trainer> list();

    Trainer getByUsername(Credentials auth, String username);
    void changePassword(Credentials auth, String newPassword);
    Trainer updateProfile(Credentials auth, Trainer updates);
    void activate(Credentials auth);
    void deactivate(Credentials auth);

    List<Trainer> listNotAssignedToTrainee(Credentials auth, String traineeUsername);
}
