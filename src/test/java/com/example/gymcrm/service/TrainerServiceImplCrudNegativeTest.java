package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TrainerServiceImplCrudNegativeTest {

    @Mock TrainerDao trainerDao;
    @Mock UserDao userDao;
    @Mock UsernameGenerator usernameGen;
    @Mock PasswordGenerator passwordGen;
    @Mock AuthService authService;

    TrainerService service;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        service = new TrainerServiceImpl(trainerDao, userDao, usernameGen, passwordGen, authService);
    }

    @Test
    void update_notFound_throws() {
        Trainer t = new Trainer(); t.setId(88L);
        when(trainerDao.findById(88L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.update(t));
    }
}
