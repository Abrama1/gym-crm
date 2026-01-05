package com.example.gymcrm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEqualsHashCodeTest {

    @Test
    void equals_hashCode_basic() {
        User a = new User(); a.setId(1L);
        a.setFirstName("John"); a.setLastName("Smith");
        a.setUsername("John.Smith"); a.setPassword("pw"); a.setActive(true);

        User b = new User(); b.setId(1L);
        b.setFirstName("John"); b.setLastName("Smith");
        b.setUsername("John.Smith"); b.setPassword("pw"); b.setActive(true);

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setId(2L);
        assertNotEquals(a, b);
        assertNotEquals(a, null);
        assertNotEquals(a, "user");
    }
}
