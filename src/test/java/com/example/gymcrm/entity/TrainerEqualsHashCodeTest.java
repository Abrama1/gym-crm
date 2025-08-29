package com.example.gymcrm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerEqualsHashCodeTest {
    @Test
    void equals_hashCode_byId() {
        User u1 = new User(); u1.setId(20L); u1.setUsername("Jane.Doe");
        Trainer a = new Trainer(); a.setId(7L); a.setUser(u1); a.setSpecialization("Strength");

        User u2 = new User(); u2.setId(21L); u2.setUsername("Jane.Doe");
        Trainer b = new Trainer(); b.setId(7L); b.setUser(u2); b.setSpecialization("Strength");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        b.setId(8L);
        assertNotEquals(a, b);
    }
}
