package com.example.gymcrm.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CredentialsPojoTest {
    @Test
    void allArgsAndSetters() {
        Credentials c = new Credentials("u","p");
        assertEquals("u", c.getUsername());
        assertEquals("p", c.getPassword());
        c.setUsername("x"); c.setPassword("y");
        assertEquals("x", c.getUsername());
        assertEquals("y", c.getPassword());
    }
}
