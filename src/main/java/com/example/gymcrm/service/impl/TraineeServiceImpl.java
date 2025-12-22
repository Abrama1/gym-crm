package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.*;
import com.example.gymcrm.service.TraineeService;
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
public class TraineeServiceImpl implements TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final UserDao userDao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final MeterRegistry meter;
    private final PasswordEncoder passwordEncoder;

    public TraineeServiceImpl(TraineeDao traineeDao,
                              TrainerDao trainerDao,
                              UserDao userDao,
                              UsernameGenerator usernameGenerator,
                              PasswordGenerator passwordGenerator,
                              MeterRegistry meter,
                              PasswordEncoder passwordEncoder) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.userDao = userDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.meter = meter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Trainee create(Trainee trainee, String firstName, String lastName, boolean active) {
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

        trainee.setUser(user);
        Trainee saved = traineeDao.save(trainee);

        meter.counter("gym_registrations_total", "role", "trainee").increment();
        if (active) meter.counter("gym_profile_activations_total","role","trainee","action","activate").increment();

        log.info("Created trainee id={} username={}", saved.getId(), username);
        return saved;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (trainee.getId() == null || traineeDao.findById(trainee.getId()).isEmpty())
            throw new NotFoundException("Trainee not found");
        Trainee saved = traineeDao.save(trainee);
        log.debug("Updated trainee id={}", saved.getId());
        return saved;
    }

    @Override
    public void delete(Long traineeId) {
        Trainee existing = traineeDao.findById(traineeId)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));
        Long userId = existing.getUser() != null ? existing.getUser().getId() : null;
        traineeDao.delete(existing);
        if (userId != null) userDao.deleteById(userId);
        log.info("Deleted trainee id={}", traineeId);
    }

    @Override @Transactional(readOnly = true)
    public Optional<Trainee> getById(Long id){ return traineeDao.findById(id); }

    @Override @Transactional(readOnly = true)
    public List<Trainee> list(){ return new ArrayList<>(traineeDao.findAll()); }

    @Override @Transactional(readOnly = true)
    public Trainee getByUsername(String username) {
        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + username));
    }

    @Override
    public void changePassword(String username, String newPassword) {
        Trainee me = getByUsername(username);
        User u = me.getUser();
        u.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(u);
        log.info("Trainee {} changed password", u.getUsername());
    }

    @Override
    public Trainee updateProfile(String username, Trainee updates) {
        Trainee me = getByUsername(username);
        me.setAddress(updates.getAddress());
        me.setDateOfBirth(updates.getDateOfBirth());
        Trainee saved = traineeDao.save(me);
        log.info("Trainee {} updated profile", me.getUser().getUsername());
        return saved;
    }

    @Override
    public void activate(String username) {
        Trainee me = getByUsername(username);
        User u = me.getUser();
        if (u.isActive()) throw new AlreadyActiveException("Trainee already active");
        u.setActive(true); userDao.save(u);
        meter.counter("gym_profile_activations_total","role","trainee","action","activate").increment();
        log.info("Trainee {} activated", u.getUsername());
    }

    @Override
    public void deactivate(String username) {
        Trainee me = getByUsername(username);
        User u = me.getUser();
        if (!u.isActive()) throw new AlreadyDeactivatedException("Trainee already deactivated");
        u.setActive(false); userDao.save(u);
        meter.counter("gym_profile_activations_total","role","trainee","action","deactivate").increment();
        log.info("Trainee {} deactivated", u.getUsername());
    }

    @Override
    public void deleteByUsername(String username) {
        Trainee me = getByUsername(username);
        Long userId = me.getUser().getId();
        traineeDao.delete(me);
        userDao.deleteById(userId);
        log.info("Trainee {} deleted", username);
    }

    @Override
    public void setTrainers(String traineeUsername, List<String> trainerUsernames) {
        Trainee me = getByUsername(traineeUsername);

        var newSet = new HashSet<Trainer>();
        for (String tu : trainerUsernames) {
            var tr = trainerDao.findByUsername(tu)
                    .orElseThrow(() -> new NotFoundException("Trainer not found: " + tu));
            newSet.add(tr);
        }
        me.getTrainers().clear();
        me.getTrainers().addAll(newSet);
        traineeDao.save(me);
        log.info("Trainee {} trainers updated: {}", traineeUsername, trainerUsernames);
    }
}
