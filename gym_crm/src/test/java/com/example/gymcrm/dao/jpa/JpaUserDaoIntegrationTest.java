package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.config.TestJpaConfig;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestJpaConfig.class)
@Transactional
class JpaUserDaoIntegrationTest {

    @Autowired private UserDao userDao;

    @Test
    void crud_and_findByUsername() {
        User u = new User();
        u.setFirstName("John"); u.setLastName("Smith");
        u.setUsername("John.Smith"); u.setPassword("pw"); u.setActive(true);
        u = userDao.save(u);

        assertTrue(userDao.findById(u.getId()).isPresent());
        assertTrue(userDao.findByUsername("john.smith").isPresent());
        List<User> all = (List<User>) userDao.findAll();
        assertEquals(1, all.size());

        userDao.deleteById(u.getId());
        assertTrue(userDao.findAll().isEmpty());
    }
}
