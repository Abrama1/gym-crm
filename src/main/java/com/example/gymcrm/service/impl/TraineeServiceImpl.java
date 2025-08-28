// src/main/java/com/example/gymcrm/service/impl/TraineeServiceImpl.java
package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.NotFoundException;
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
    private final UserDao userDao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public TraineeServiceImpl(TraineeDao traineeDao, UserDao userDao,
                              UsernameGenerator usernameGenerator, PasswordGenerator passwordGenerator) {
        this.traineeDao = traineeDao;
        this.userDao = userDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
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
        traineeDao.deleteById(traineeId);
        if (userId != null) userDao.deleteById(userId);
        log.info("Deleted trainee id={}", traineeId);
    }

    @Override @Transactional(readOnly = true)
    public Optional<Trainee> getById(Long id){ return traineeDao.findById(id); }

    @Override @Transactional(readOnly = true)
    public List<Trainee> list(){ return new ArrayList<>(traineeDao.findAll()); }
}
