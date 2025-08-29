package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.*;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
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
    private final AuthService authService;

    public TrainerServiceImpl(TrainerDao trainerDao, UserDao userDao,
                              UsernameGenerator usernameGenerator, PasswordGenerator passwordGenerator,
                              AuthService authService) {
        this.trainerDao = trainerDao;
        this.userDao = userDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.authService = authService;
    }

    @Override
    public Trainer create(Trainer trainer, String firstName, String lastName, boolean active) {
        String username = usernameGenerator.generateUnique(firstName, lastName);
        String password = passwordGenerator.random10();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(active);
        userDao.save(user);

        trainer.setUser(user);
        Trainer saved = trainerDao.save(trainer);
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

    // auth-gated

    @Override @Transactional(readOnly = true)
    public Trainer getByUsername(Credentials auth, String username) {
        var me = authService.authenticateTrainer(auth);
        if (!me.getUser().getUsername().equalsIgnoreCase(username))
            throw new AuthFailedException("Access denied to other trainer profile");
        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + username));
    }

    @Override
    public void changePassword(Credentials auth, String newPassword) {
        var me = authService.authenticateTrainer(auth);
        User u = me.getUser();
        u.setPassword(newPassword);
        userDao.save(u);
        log.info("Trainer {} changed password", u.getUsername());
    }

    @Override
    public Trainer updateProfile(Credentials auth, Trainer updates) {
        var me = authService.authenticateTrainer(auth);
        me.setSpecialization(updates.getSpecialization());
        Trainer saved = trainerDao.save(me);
        log.info("Trainer {} updated profile", me.getUser().getUsername());
        return saved;
    }

    @Override
    public void activate(Credentials auth) {
        var me = authService.authenticateTrainer(auth);
        User u = me.getUser();
        if (u.isActive()) throw new AlreadyActiveException("Trainer already active");
        u.setActive(true); userDao.save(u);
        log.info("Trainer {} activated", u.getUsername());
    }

    @Override
    public void deactivate(Credentials auth) {
        var me = authService.authenticateTrainer(auth);
        User u = me.getUser();
        if (!u.isActive()) throw new AlreadyDeactivatedException("Trainer already deactivated");
        u.setActive(false); userDao.save(u);
        log.info("Trainer {} deactivated", u.getUsername());
    }

    @Override @Transactional(readOnly = true)
    public List<Trainer> listNotAssignedToTrainee(Credentials auth, String traineeUsername) {
        // require any authenticated trainer to view this
        authService.authenticateTrainer(auth);
        return new ArrayList<>(trainerDao.listNotAssignedToTrainee(traineeUsername));
    }
}
