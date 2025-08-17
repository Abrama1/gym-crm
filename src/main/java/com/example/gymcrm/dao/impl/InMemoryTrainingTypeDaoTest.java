package com.example.gymcrm.dao.impl;

import com.example.gymcrm.domain.TrainingType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTrainingTypeDaoTest {

    @SuppressWarnings("unchecked")
    private void injectStorage(InMemoryTrainingTypeDao dao, Map<String, TrainingType> map) throws Exception {
        Field f = InMemoryTrainingTypeDao.class.getDeclaredField("storage");
        f.setAccessible(true);
        f.set(dao, map);
    }

    @Test
    void save_findAll_findByName_flow() throws Exception {
        InMemoryTrainingTypeDao dao = new InMemoryTrainingTypeDao();
        Map<String, TrainingType> map = new ConcurrentHashMap<>();
        injectStorage(dao, map);

        TrainingType tt = new TrainingType();
        tt.setName("Cardio");
        dao.save(tt);

        assertEquals(1, dao.findAll().size());
        assertTrue(dao.findByName("Cardio").isPresent());
    }
}
