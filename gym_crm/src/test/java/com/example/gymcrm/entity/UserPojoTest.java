package com.example.gymcrm.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserPojoTest {
    @Test
    void gettersSetters() {
        User u = new User();
        u.setId(1L);
        u.setFirstName("John");
        u.setLastName("Smith");
        u.setUsername("John.Smith");
        u.setPassword("pw");
        u.setActive(true);

        assertEquals(1L, u.getId());
        assertEquals("John", u.getFirstName());
        assertEquals("Smith", u.getLastName());
        assertEquals("John.Smith", u.getUsername());
        assertEquals("pw", u.getPassword());
        assertTrue(u.isActive());
    }
}
