package com.example.gymcrm.service;

import com.example.gymcrm.entity.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeService {
    Trainee create(Trainee trainee, String firstName, String lastName, boolean active);
    Trainee update(Trainee trainee);
    void delete(Long traineeId);

    Optional<Trainee> getById(Long id);
    List<Trainee> list();

    Trainee getByUsername(String username);
    void changePassword(String username, String newPassword);
    Trainee updateProfile(String username, Trainee updates);
    void activate(String username);
    void deactivate(String username);
    void deleteByUsername(String username);
    void setTrainers(String traineeUsername, List<String> trainerUsernames);
}
