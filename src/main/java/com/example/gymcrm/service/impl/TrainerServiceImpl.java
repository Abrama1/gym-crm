package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.*;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TrainerServiceImpl implements TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerDao trainerDao;
    private final UserDao userDao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final MeterRegistry meter;
    private final PasswordEncoder passwordEncoder;

    public TrainerServiceImpl(TrainerDao trainerDao,
                              UserDao userDao,
                              UsernameGenerator usernameGenerator,
                              PasswordGenerator passwordGenerator,
                              MeterRegistry meter,
                              PasswordEncoder passwordEncoder) {
        this.trainerDao = trainerDao;
        this.userDao = userDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.meter = meter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Trainer create(Trainer trainer, String firstName, String lastName, boolean active) {
        String username = usernameGenerator.generateUnique(firstName, lastName);
        String rawPassword = passwordGenerator.random10();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPlainPassword(rawPassword);               // returned once
        user.setPassword(passwordEncoder.encode(rawPassword)); // stored as hash
        user.setActive(active);
        userDao.save(user);

        trainer.setUser(user);
        Trainer saved = trainerDao.save(trainer);

        meter.counter("gym_registrations_total", "role", "trainer").increment();
        if (active) {
            meter.counter("gym_profile_activations_total", "role", "trainer", "action", "activate").increment();
        }

        log.info("Created trainer id={} username={}", saved.getId(), username);
        return saved;
    }

    @Override
    public Trainer update(Trainer trainer) {
        if (trainer.getId() == null || trainerDao.findById(trainer.getId()).isEmpty())
            throw new NotFoundException("Trainer not found");
        Trainer saved = trainerDao.save(trainer);
        log.debug("Updated trainer id={}", saved.getId());
        return saved;
    }

    @Override @Transactional(readOnly = true)
    public Optional<Trainer> getById(Long id){ return trainerDao.findById(id); }

    @Override @Transactional(readOnly = true)
    public List<Trainer> list(){ return new ArrayList<>(trainerDao.findAll()); }

    // ---- auth-gated (principal-based) ----

    @Override @Transactional(readOnly = true)
    public Trainer getByUsername(Credentials auth, String username) {
        String principal = auth.getUsername();
        if (principal == null || !principal.equalsIgnoreCase(username))
            throw new AuthFailedException("Access denied to other trainer profile");
        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + username));
    }

    @Override
    public void changePassword(Credentials auth, String newPassword) {
        String principal = auth.getUsername();
        Trainer me = trainerDao.findByUsername(principal)
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + principal));
        User u = me.getUser();
        u.setPassword(passwordEncoder.encode(newPassword));  // hash on change
        userDao.save(u);
        log.info("Trainer {} changed password", u.getUsername());
    }

    @Override
    public Trainer updateProfile(Credentials auth, Trainer updates) {
        String principal = auth.getUsername();
        Trainer me = trainerDao.findByUsername(principal)
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + principal));

        // specialization is READ-ONLY per REST spec -> ignore updates.getSpecialization()

        if (updates != null && updates.getUser() != null) {
            User src = updates.getUser();
            User dst = me.getUser();

            if (src.getFirstName() != null) dst.setFirstName(src.getFirstName());
            if (src.getLastName()  != null) dst.setLastName(src.getLastName());
            if (src.isActive() != dst.isActive()) dst.setActive(src.isActive());

            userDao.save(dst);
        }

        Trainer saved = trainerDao.save(me);
        log.info("Trainer {} updated profile (name/active)", me.getUser().getUsername());
        return saved;
    }

    @Override
    public void activate(Credentials auth) {
        String principal = auth.getUsername();
        Trainer me = trainerDao.findByUsername(principal)
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + principal));
        User u = me.getUser();
        if (u.isActive()) throw new AlreadyActiveException("Trainer already active");
        u.setActive(true); userDao.save(u);
        meter.counter("gym_profile_activations_total", "role", "trainer", "action", "activate").increment();
        log.info("Trainer {} activated", u.getUsername());
    }

    @Override
    public void deactivate(Credentials auth) {
        String principal = auth.getUsername();
        Trainer me = trainerDao.findByUsername(principal)
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + principal));
        User u = me.getUser();
        if (!u.isActive()) throw new AlreadyDeactivatedException("Trainer already deactivated");
        u.setActive(false); userDao.save(u);
        meter.counter("gym_profile_activations_total", "role", "trainer", "action", "deactivate").increment();
        log.info("Trainer {} deactivated", u.getUsername());
    }

    @Override @Transactional(readOnly = true)
    public List<Trainer> listNotAssignedToTrainee(Credentials auth, String traineeUsername) {
        // Any authenticated trainer can call this; verify principal exists as a trainer
        String principal = auth.getUsername();
        trainerDao.findByUsername(principal)
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + principal));
        return new ArrayList<>(trainerDao.listNotAssignedToTrainee(traineeUsername));
    }
}
