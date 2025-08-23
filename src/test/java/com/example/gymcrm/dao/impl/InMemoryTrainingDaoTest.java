package com.example.gymcrm.dao.impl;

import com.example.gymcrm.domain.Training;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTrainingDaoTest {

    @SuppressWarnings("unchecked")
    private void injectStorage(InMemoryTrainingDao dao, Map<Long, Training> map) throws Exception {
        Field f = InMemoryTrainingDao.class.getDeclaredField("storage");
        f.setAccessible(true);
        f.set(dao, map);
    }

    @Test
    void save_find_flow() throws Exception {
        InMemoryTrainingDao dao = new InMemoryTrainingDao();
        Map<Long, Training> map = new ConcurrentHashMap<>();
        injectStorage(dao, map);

        Training t = new Training();
        t.setTrainingName("Cardio Morning");
        t.setDurationMinutes(30);
        t.setTrainingDate(LocalDateTime.now());

        Training saved = dao.save(t);
        assertNotNull(saved.getId());
        assertTrue(dao.findById(saved.getId()).isPresent());
        assertEquals(1, dao.findAll().size());
    }
}
