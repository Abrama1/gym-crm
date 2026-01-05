package com.example.gymcrm.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrainingCriteriaPojoTest {
    @Test
    void noArgsAllArgsSetters() {
        var from = LocalDateTime.now().minusDays(1);
        var to = LocalDateTime.now().plusDays(1);

        TrainingCriteria a = new TrainingCriteria(from, to, "Cardio", "%jane%");
        assertEquals(from, a.getFrom());
        assertEquals(to, a.getTo());
        assertEquals("Cardio", a.getTrainingType());
        assertEquals("%jane%", a.getOtherPartyNameLike());

        TrainingCriteria b = new TrainingCriteria();
        b.setFrom(from); b.setTo(to); b.setTrainingType("Strength"); b.setOtherPartyNameLike("%john%");
        assertEquals("Strength", b.getTrainingType());
        assertEquals("%john%", b.getOtherPartyNameLike());
    }
}
