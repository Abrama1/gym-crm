package com.example.gymcrm.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsEqualsHashCodeTest {

    @Test
    void equals_hashCode_toString() {
        Credentials a = new Credentials("John.Smith","pw");
        Credentials b = new Credentials("John.Smith","pw");

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, new Credentials("john.smith","pw2"));
        assertNotEquals(a, null);
        assertNotEquals(a, "creds");

        assertTrue(a.toString().toLowerCase().contains("john.smith"));
    }
}
