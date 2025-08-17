package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.User;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerDao trainerDao;
    private UserDao userDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired public void setTrainerDao(TrainerDao trainerDao) { this.trainerDao = trainerDao; }
    @Autowired public void setUserDao(UserDao userDao) { this.userDao = userDao; }
    @Autowired public void setUsernameGenerator(UsernameGenerator usernameGenerator) { this.usernameGenerator = usernameGenerator; }
    @Autowired public void setPasswordGenerator(PasswordGenerator passwordGenerator) { this.passwordGenerator = passwordGenerator; }

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

        trainer.setUserId(user.getId());
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

    @Override public Optional<Trainer> getById(Long id) { return trainerDao.findById(id); }
    @Override public List<Trainer> list() { return new ArrayList<>(trainerDao.findAll()); }
}
