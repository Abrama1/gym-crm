package com.example.gymcrm.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrainingTypePojoTest {
    @Test
    void gettersSetters() {
        TrainingType t = new TrainingType();
        t.setId(2L);
        t.setName("Cardio");
        assertEquals(2L, t.getId());
        assertEquals("Cardio", t.getName());
    }
}
