package com.example.gymcrm.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    @Test
    void random10_returns10Chars_andAlnum() {
        PasswordGenerator gen = new PasswordGenerator();
        String p = gen.random10();
        assertEquals(10, p.length());

        for (char c : p.toCharArray()) {
            boolean ok = (c >= 'A' && c <= 'Z') ||
                    (c >= 'a' && c <= 'z') ||
                    (c >= '0' && c <= '9');
            assertTrue(ok, "Non-alphanumeric char found: " + c);
        }
    }
}
