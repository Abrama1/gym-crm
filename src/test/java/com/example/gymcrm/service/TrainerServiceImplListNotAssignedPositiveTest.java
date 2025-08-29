package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainerServiceImplListNotAssignedPositiveTest {

    @Mock TrainerDao trainerDao;
    @Mock UserDao userDao;
    @Mock UsernameGenerator usernameGen;
    @Mock PasswordGenerator passwordGen;
    @Mock AuthService authService;

    TrainerService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new TrainerServiceImpl(trainerDao, userDao, usernameGen, passwordGen, authService);

        User u = new User();
        u.setUsername("Jane.Doe");
        u.setActive(true);
        u.setPassword("pw");
        Trainer me = new Trainer(); me.setUser(u);

        when(authService.authenticateTrainer(new Credentials("Jane.Doe","pw"))).thenReturn(me);
    }

    @Test
    void listsFromDao() {
        Trainer t = new Trainer();
        when(trainerDao.listNotAssignedToTrainee("John.Smith")).thenReturn(List.of(t));
        var res = service.listNotAssignedToTrainee(new Credentials("Jane.Doe","pw"), "John.Smith");
        assertEquals(1, res.size());
        verify(trainerDao).listNotAssignedToTrainee("John.Smith");
    }
}
