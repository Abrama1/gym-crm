package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.config.TestJpaConfig;
import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.entity.TrainingType;
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
class JpaTrainingTypeDaoIntegrationTest {

    @Autowired private TrainingTypeDao dao;

    @Test
    void save_findByName_findAll() {
        TrainingType tt = new TrainingType(); tt.setName("Cardio");
        dao.save(tt);
        assertTrue(dao.findByName("Cardio").isPresent());
        assertEquals(1, dao.findAll().size());
    }
}
