package com.example.gymcrm.service;

import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;

public interface AuthService {
    Trainee authenticateTrainee(Credentials credentials);
    Trainer authenticateTrainer(Credentials credentials);
}
