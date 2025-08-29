package com.example.gymcrm.service;

import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeService {
    Trainee create(Trainee trainee, String firstName, String lastName, boolean active);
    Trainee update(Trainee trainee);
    void delete(Long traineeId);
    Optional<Trainee> getById(Long traineeId);
    List<Trainee> list();

    Trainee getByUsername(Credentials auth, String username);
    void changePassword(Credentials auth, String newPassword);
    Trainee updateProfile(Credentials auth, Trainee updates);
    void activate(Credentials auth);
    void deactivate(Credentials auth);
    void deleteByUsername(Credentials auth, String username);
    void setTrainers(Credentials auth, String traineeUsername, List<String> trainerUsernames);
}
