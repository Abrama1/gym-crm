// src/main/java/com/example/gymcrm/service/impl/TrainerServiceImpl.java
package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.NotFoundException;
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

    public TrainerServiceImpl(TrainerDao trainerDao, UserDao userDao,
                              UsernameGenerator usernameGenerator, PasswordGenerator passwordGenerator) {
        this.trainerDao = trainerDao;
        this.userDao = userDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
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
}
