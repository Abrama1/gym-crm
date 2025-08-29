package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.*;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
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
    private final AuthService authService;

    public TraineeServiceImpl(TraineeDao traineeDao, TrainerDao trainerDao, UserDao userDao,
                              UsernameGenerator usernameGenerator, PasswordGenerator passwordGenerator,
                              AuthService authService) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.userDao = userDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.authService = authService;
    }

    @Override
    public Trainee create(Trainee trainee, String firstName, String lastName, boolean active) {
        String username = usernameGenerator.generateUnique(firstName, lastName);
        String password = passwordGenerator.random10();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(active);
        userDao.save(user);

        trainee.setUser(user);
        Trainee saved = traineeDao.save(trainee);
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
        traineeDao.delete(existing); // cascades remove trainings
        if (userId != null) userDao.deleteById(userId);
        log.info("Deleted trainee id={}", traineeId);
    }

    @Override @Transactional(readOnly = true)
    public Optional<Trainee> getById(Long id){ return traineeDao.findById(id); }

    @Override @Transactional(readOnly = true)
    public List<Trainee> list(){ return new ArrayList<>(traineeDao.findAll()); }

    // ---- auth-gated ----

    @Override @Transactional(readOnly = true)
    public Trainee getByUsername(Credentials auth, String username) {
        var me = authService.authenticateTrainee(auth);
        if (!me.getUser().getUsername().equalsIgnoreCase(username))
            throw new AuthFailedException("Access denied to other trainee profile");
        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + username));
    }

    @Override
    public void changePassword(Credentials auth, String newPassword) {
        var me = authService.authenticateTrainee(auth);
        User u = me.getUser();
        u.setPassword(newPassword);
        userDao.save(u);
        log.info("Trainee {} changed password", u.getUsername());
    }

    @Override
    public Trainee updateProfile(Credentials auth, Trainee updates) {
        var me = authService.authenticateTrainee(auth);
        // Only allow editing own profile fields (address, dateOfBirth)
        me.setAddress(updates.getAddress());
        me.setDateOfBirth(updates.getDateOfBirth());
        Trainee saved = traineeDao.save(me);
        log.info("Trainee {} updated profile", me.getUser().getUsername());
        return saved;
    }

    @Override
    public void activate(Credentials auth) {
        var me = authService.authenticateTrainee(auth);
        User u = me.getUser();
        if (u.isActive()) throw new AlreadyActiveException("Trainee already active");
        u.setActive(true); userDao.save(u);
        log.info("Trainee {} activated", u.getUsername());
    }

    @Override
    public void deactivate(Credentials auth) {
        var me = authService.authenticateTrainee(auth);
        User u = me.getUser();
        if (!u.isActive()) throw new AlreadyDeactivatedException("Trainee already deactivated");
        u.setActive(false); userDao.save(u);
        log.info("Trainee {} deactivated", u.getUsername());
    }

    @Override
    public void deleteByUsername(Credentials auth, String username) {
        var me = authService.authenticateTrainee(auth);
        if (!me.getUser().getUsername().equalsIgnoreCase(username))
            throw new AuthFailedException("Cannot delete another trainee");
        // cascades remove trainings
        Long userId = me.getUser().getId();
        traineeDao.delete(me);
        userDao.deleteById(userId);
        log.info("Trainee {} deleted", username);
    }

    @Override
    public void setTrainers(Credentials auth, String traineeUsername, List<String> trainerUsernames) {
        var me = authService.authenticateTrainee(auth);
        if (!me.getUser().getUsername().equalsIgnoreCase(traineeUsername))
            throw new AuthFailedException("Cannot modify trainers for another trainee");

        // replace the set
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
