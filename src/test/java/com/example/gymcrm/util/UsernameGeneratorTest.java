package com.example.gymcrm.util;

import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UsernameGeneratorTest {

    @Mock UserDao userDao;
    private UsernameGenerator generator;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        generator = new UsernameGenerator();
        generator.setUserDao(userDao);
    }

    @Test
    void generateUnique_noCollision_returnsBase() {
        when(userDao.findByUsername("John.Smith")).thenReturn(Optional.empty());
        String u = generator.generateUnique("John", "Smith");
        assertEquals("John.Smith", u);
    }

    @Test
    void generateUnique_withCollision_appendsSerial() {
        when(userDao.findByUsername("John.Smith"))
                .thenReturn(Optional.of(new User())); // first collision
        when(userDao.findByUsername("John.Smith1"))
                .thenReturn(Optional.empty()); // second is free

        String u = generator.generateUnique("John", "Smith");
        assertEquals("John.Smith1", u);
    }
}
