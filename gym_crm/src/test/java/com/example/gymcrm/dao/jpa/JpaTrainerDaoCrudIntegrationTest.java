package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.config.TestJpaConfig;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestJpaConfig.class)
@Transactional
class JpaTrainerDaoCrudIntegrationTest {

    @Autowired private TrainerDao trainerDao;
    @Autowired private UserDao userDao;

    @Test
    void save_finders() {
        User u = new User();
        u.setFirstName("Jane"); u.setLastName("Doe");
        u.setUsername("Jane.Doe"); u.setPassword("pw"); u.setActive(true);
        u = userDao.save(u);

        Trainer tr = new Trainer();
        tr.setUser(u); tr.setSpecialization("Strength");
        tr = trainerDao.save(tr);

        assertTrue(trainerDao.findByUsername("jane.doe").isPresent());
        assertTrue(trainerDao.findByUserId(u.getId()).isPresent());
        assertEquals(1, trainerDao.findAll().size());
    }
}
