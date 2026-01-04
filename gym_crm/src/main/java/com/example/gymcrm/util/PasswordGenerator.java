package com.example.gymcrm.util;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class PasswordGenerator {
    private static final String ALPH = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LEN = 10;
    private final SecureRandom rnd = new SecureRandom();

    public String random10() {
        StringBuilder b = new StringBuilder(LEN);
        for (int i = 0; i < LEN; i++) b.append(ALPH.charAt(rnd.nextInt(ALPH.length())));
        return b.toString();
    }
}
