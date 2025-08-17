package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.User;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.TraineeService;
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
public class TraineeServiceImpl implements TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeDao traineeDao;
    private UserDao userDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired public void setTraineeDao(TraineeDao traineeDao) { this.traineeDao = traineeDao; }
    @Autowired public void setUserDao(UserDao userDao) { this.userDao = userDao; }
    @Autowired public void setUsernameGenerator(UsernameGenerator usernameGenerator) { this.usernameGenerator = usernameGenerator; }
    @Autowired public void setPasswordGenerator(PasswordGenerator passwordGenerator) { this.passwordGenerator = passwordGenerator; }

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

        trainee.setUserId(user.getId());
        Trainee saved = traineeDao.save(trainee);
        log.info("Created trainee id={} username={}", saved.getId(), username); // never log password
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
        traineeDao.deleteById(traineeId);
        if (existing.getUserId() != null) userDao.deleteById(existing.getUserId());
        log.info("Deleted trainee id={}", traineeId);
    }

    @Override public Optional<Trainee> getById(Long traineeId) { return traineeDao.findById(traineeId); }
    @Override public List<Trainee> list() { return new ArrayList<>(traineeDao.findAll()); }
}
