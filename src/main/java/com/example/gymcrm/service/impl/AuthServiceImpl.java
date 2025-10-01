package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.service.AuthService;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserDao userDao;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final MeterRegistry meter;

    public AuthServiceImpl(UserDao userDao,
                           TraineeDao traineeDao,
                           TrainerDao trainerDao,
                           MeterRegistry meter) {
        this.userDao = userDao;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.meter = meter;
    }

    @Override
    public Trainee authenticateTrainee(Credentials credentials) {
        User user = userDao.findByUsername(credentials.getUsername())
                .orElse(null);
        if (user == null) {
            markAuth("trainee", false);
            throw fail("Unknown username");
        }
        if (!user.isActive()) {
            markAuth("trainee", false);
            throw fail("User is deactivated");
        }
        if (!safeEquals(user.getPassword(), credentials.getPassword())) {
            markAuth("trainee", false);
            throw fail("Password mismatch");
        }

        Trainee t = traineeDao.findByUserId(user.getId()).orElse(null);
        if (t == null) {
            markAuth("trainee", false);
            throw fail("Trainee profile not found for user");
        }

        markAuth("trainee", true);
        return t;
    }

    @Override
    public Trainer authenticateTrainer(Credentials credentials) {
        User user = userDao.findByUsername(credentials.getUsername())
                .orElse(null);
        if (user == null) {
            markAuth("trainer", false);
            throw fail("Unknown username");
        }
        if (!user.isActive()) {
            markAuth("trainer", false);
            throw fail("User is deactivated");
        }
        if (!safeEquals(user.getPassword(), credentials.getPassword())) {
            markAuth("trainer", false);
            throw fail("Password mismatch");
        }

        Trainer tr = trainerDao.findByUserId(user.getId()).orElse(null);
        if (tr == null) {
            markAuth("trainer", false);
            throw fail("Trainer profile not found for user");
        }

        markAuth("trainer", true);
        return tr;
    }

    private void markAuth(String role, boolean success) {
        meter.counter("gym_auth_attempts_total",
                "role", role,
                "outcome", success ? "success" : "failure").increment();
    }

    private AuthFailedException fail(String msg) {
        log.warn("Authentication failed: {}", msg);
        return new AuthFailedException(msg);
    }

    private boolean safeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }
}
