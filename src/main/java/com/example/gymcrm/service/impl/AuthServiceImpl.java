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

    public AuthServiceImpl(UserDao userDao, TraineeDao traineeDao, TrainerDao trainerDao) {
        this.userDao = userDao;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    @Override
    public Trainee authenticateTrainee(Credentials credentials) {
        User user = userDao.findByUsername(credentials.getUsername())
                .orElseThrow(() -> fail("Unknown username"));

        if (!user.isActive()) throw fail("User is deactivated");
        if (!safeEquals(user.getPassword(), credentials.getPassword()))
            throw fail("Password mismatch");

        // We don’t have DAO findByUserId in the interface; to keep the surface stable,
        // we scan the list. With JPA this becomes a single query internally.
        return traineeDao.findAll().stream()
                .filter(t -> t.getUser() != null && t.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> fail("Trainee profile not found for user"));
    }

    @Override
    public Trainer authenticateTrainer(Credentials credentials) {
        User user = userDao.findByUsername(credentials.getUsername())
                .orElseThrow(() -> fail("Unknown username"));

        if (!user.isActive()) throw fail("User is deactivated");
        if (!safeEquals(user.getPassword(), credentials.getPassword()))
            throw fail("Password mismatch");

        return trainerDao.findAll().stream()
                .filter(t -> t.getUser() != null && t.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> fail("Trainer profile not found for user"));
    }

    private AuthFailedException fail(String msg) {
        log.warn("Authentication failed: {}", msg);
        return new AuthFailedException(msg);
    }

    private boolean safeEquals(String a, String b) {
        if (a == null || b == null) return false;
        // basic constant-time-ish compare for demo purposes
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }
}
