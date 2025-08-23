package com.example.gymcrm.dao.impl;

import com.example.gymcrm.domain.User;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserDaoTest {

    @SuppressWarnings("unchecked")
    private void injectStorage(InMemoryUserDao dao, Map<Long, User> map) throws Exception {
        Field f = InMemoryUserDao.class.getDeclaredField("storage");
        f.setAccessible(true);
        f.set(dao, map);
    }

    @Test
    void save_find_delete_flow() throws Exception {
        InMemoryUserDao dao = new InMemoryUserDao();
        Map<Long, User> map = new ConcurrentHashMap<>();
        injectStorage(dao, map);

        User u = new User();
        u.setFirstName("John"); u.setLastName("Smith");
        u.setUsername("John.Smith"); u.setPassword("secret"); u.setActive(true);

        User saved = dao.save(u);
        assertNotNull(saved.getId());

        assertTrue(dao.findById(saved.getId()).isPresent());
        assertTrue(dao.findByUsername("John.Smith").isPresent());
        assertEquals(1, dao.findAll().size());

        dao.deleteById(saved.getId());
        assertTrue(dao.findAll().isEmpty());
    }
}
