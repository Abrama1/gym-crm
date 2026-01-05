package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
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
        user.setPlainPassword(rawPassword);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setActive(active);
        userDao.save(user);

        trainer.setUser(user);
        Trainer saved = trainerDao.save(trainer);

        meter.counter("gym_registrations_total", "role", "trainer").increment();
        if (active) meter.counter("gym_profile_activations_total","role","trainer","action","activate").increment();

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

    @Override @Transactional(readOnly = true)
    public Trainer getByUsername(String username) {
        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + username));
    }

    @Override
    public void changePassword(String username, String newPassword) {
        Trainer me = getByUsername(username);
        User u = me.getUser();
        u.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(u);
        log.info("Trainer {} changed password", u.getUsername());
    }

    @Override
    public Trainer updateProfile(String username, Trainer updates) {
        Trainer me = getByUsername(username);

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
    public void activate(String username) {
        Trainer me = getByUsername(username);
        User u = me.getUser();
        if (u.isActive()) throw new AlreadyActiveException("Trainer already active");
        u.setActive(true); userDao.save(u);
        meter.counter("gym_profile_activations_total","role","trainer","action","activate").increment();
        log.info("Trainer {} activated", u.getUsername());
    }

    @Override
    public void deactivate(String username) {
        Trainer me = getByUsername(username);
        User u = me.getUser();
        if (!u.isActive()) throw new AlreadyDeactivatedException("Trainer already deactivated");
        u.setActive(false); userDao.save(u);
        meter.counter("gym_profile_activations_total","role","trainer","action","deactivate").increment();
        log.info("Trainer {} deactivated", u.getUsername());
    }

    @Override @Transactional(readOnly = true)
    public List<Trainer> listNotAssignedToTrainee(String traineeUsername) {
        return new ArrayList<>(trainerDao.listNotAssignedToTrainee(traineeUsername));
    }
}
