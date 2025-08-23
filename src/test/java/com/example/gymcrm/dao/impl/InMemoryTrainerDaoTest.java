package com.example.gymcrm.dao.impl;

import com.example.gymcrm.domain.Trainer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTrainerDaoTest {

    @SuppressWarnings("unchecked")
    private void injectStorage(InMemoryTrainerDao dao, Map<Long, Trainer> map) throws Exception {
        Field f = InMemoryTrainerDao.class.getDeclaredField("storage");
        f.setAccessible(true);
        f.set(dao, map);
    }

    @Test
    void save_find_flow() throws Exception {
        InMemoryTrainerDao dao = new InMemoryTrainerDao();
        Map<Long, Trainer> map = new ConcurrentHashMap<>();
        injectStorage(dao, map);

        Trainer t = new Trainer();
        t.setSpecialization("Strength");
        Trainer saved = dao.save(t);

        assertNotNull(saved.getId());
        assertTrue(dao.findById(saved.getId()).isPresent());
        assertEquals(1, dao.findAll().size());
    }
}
