package com.example.gymcrm.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeEqualsHashCodeTest {
    @Test
    void equals_hashCode_byId() {
        User u1 = new User(); u1.setId(10L); u1.setUsername("John.Smith");
        Trainee a = new Trainee(); a.setId(5L); a.setUser(u1);
        a.setAddress("City"); a.setDateOfBirth(LocalDate.of(2000,1,1));

        User u2 = new User(); u2.setId(11L); u2.setUsername("John.Smith");
        Trainee b = new Trainee(); b.setId(5L); b.setUser(u2);
        b.setAddress("City"); b.setDateOfBirth(LocalDate.of(2000,1,1));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        b.setId(6L);
        assertNotEquals(a, b);
    }
}
