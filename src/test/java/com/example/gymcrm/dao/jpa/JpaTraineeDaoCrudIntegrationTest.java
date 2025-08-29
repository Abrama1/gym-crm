package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.config.TestJpaConfig;
import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestJpaConfig.class)
@Transactional
class JpaTraineeDaoCrudIntegrationTest {

    @Autowired private TraineeDao traineeDao;
    @Autowired private UserDao userDao;

    @Test
    void save_findByUsername_findByUserId_delete() {
        User u = new User();
        u.setFirstName("John"); u.setLastName("Smith");
        u.setUsername("John.Smith"); u.setPassword("pw"); u.setActive(true);
        u = userDao.save(u);

        Trainee t = new Trainee();
        t.setUser(u); t.setAddress("City"); t.setDateOfBirth(LocalDate.of(2000,1,1));
        t = traineeDao.save(t);

        assertTrue(traineeDao.findByUsername("john.smith").isPresent());
        assertTrue(traineeDao.findByUserId(u.getId()).isPresent());
        assertEquals(1, traineeDao.findAll().size());

        traineeDao.deleteById(t.getId());
        assertTrue(traineeDao.findAll().isEmpty());
    }
}
