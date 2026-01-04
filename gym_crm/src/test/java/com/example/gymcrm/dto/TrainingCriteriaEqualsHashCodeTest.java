package com.example.gymcrm.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrainingCriteriaEqualsHashCodeTest {

    @Test
    void equals_hashCode_and_toString_coverBranches() {
        var from = LocalDateTime.of(2024,1,1,8,0);
        var to   = LocalDateTime.of(2024,1,8,8,0);

        TrainingCriteria a = new TrainingCriteria(from, to, "Cardio", "%jane%");
        TrainingCriteria b = new TrainingCriteria(from, to, "Cardio", "%jane%");

        // reflexive, symmetric, hash
        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        // not equal: each field difference
        assertNotEquals(a, new TrainingCriteria(null, to, "Cardio", "%jane%"));
        assertNotEquals(a, new TrainingCriteria(from, null, "Cardio", "%jane%"));
        assertNotEquals(a, new TrainingCriteria(from, to, "Strength", "%jane%"));
        assertNotEquals(a, new TrainingCriteria(from, to, "Cardio", "%john%"));

        // type & null guards
        assertNotEquals(a, "not-a-criteria");
        assertNotEquals(a, null);

        // toString is generated — just ensure it contains a key field
        assertTrue(a.toString().toLowerCase().contains("cardio"));
    }
}
