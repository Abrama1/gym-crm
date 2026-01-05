package com.example.gymcrm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeEqualsHashCodeTest {
    @Test
    void equals_hashCode_byId() {
        TrainingType a = new TrainingType(); a.setId(3L); a.setName("Cardio");
        TrainingType b = new TrainingType(); b.setId(3L); b.setName("Cardio");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        b.setId(4L);
        assertNotEquals(a, b);
    }
}
