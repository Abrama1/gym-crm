package com.example.gymcrm.service;

import com.example.gymcrm.domain.Trainee;
import java.util.List;
import java.util.Optional;

public interface TraineeService {
    Trainee create(Trainee trainee, String firstName, String lastName, boolean active);
    Trainee update(Trainee trainee);
    void delete(Long traineeId);
    Optional<Trainee> getById(Long traineeId);
    List<Trainee> list();
}
