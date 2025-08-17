package com.example.gymcrm.dao.impl;

import com.example.gymcrm.domain.Trainee;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTraineeDaoTest {

    @SuppressWarnings("unchecked")
    private void injectStorage(InMemoryTraineeDao dao, Map<Long, Trainee> map) throws Exception {
        Field f = InMemoryTraineeDao.class.getDeclaredField("storage");
        f.setAccessible(true);
        f.set(dao, map);
    }

    @Test
    void save_find_delete_flow() throws Exception {
        InMemoryTraineeDao dao = new InMemoryTraineeDao();
        Map<Long, Trainee> map = new ConcurrentHashMap<>();
        injectStorage(dao, map);

        Trainee t = new Trainee();
        t.setDateOfBirth(LocalDate.of(2000,1,1));
        t.setAddress("Tbilisi");
        Trainee saved = dao.save(t);

        assertNotNull(saved.getId());
        assertTrue(dao.findById(saved.getId()).isPresent());
        assertEquals(1, dao.findAll().size());

        dao.deleteById(saved.getId());
        assertTrue(dao.findAll().isEmpty());
    }
}
