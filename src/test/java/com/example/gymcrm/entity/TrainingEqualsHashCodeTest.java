package com.example.gymcrm.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrainingEqualsHashCodeTest {

    @Test
    void equals_hashCode_byId() {
        Training t1 = new Training(); t1.setId(9L);
        t1.setTrainingName("Run"); t1.setTrainingDate(LocalDateTime.now()); t1.setDurationMinutes(30);

        Training t2 = new Training(); t2.setId(9L);
        t2.setTrainingName("Run"); t2.setTrainingDate(t1.getTrainingDate()); t2.setDurationMinutes(30);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        t2.setId(10L);
        assertNotEquals(t1, t2);
    }
}
