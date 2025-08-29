package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TraineeServiceImplUpdatePositiveTest {

    @Mock TraineeDao traineeDao;
    @Mock TrainerDao trainerDao;
    @Mock UserDao userDao;
    @Mock UsernameGenerator usernameGen;
    @Mock PasswordGenerator passwordGen;
    @Mock AuthService authService;

    TraineeService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new TraineeServiceImpl(traineeDao, trainerDao, userDao, usernameGen, passwordGen, authService);
    }

    @Test
    void update_updatesFields() {
        Trainee existing = new Trainee();
        existing.setId(10L);
        existing.setUser(new User());
        existing.setAddress("Old");
        existing.setDateOfBirth(LocalDate.of(1990, 1, 1));

        when(traineeDao.findById(10L)).thenReturn(Optional.of(existing));
        when(traineeDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Trainee update = new Trainee();
        update.setId(10L);
        update.setAddress("New");
        update.setDateOfBirth(LocalDate.of(2000, 2, 2));

        Trainee saved = service.update(update);

        assertEquals("New", saved.getAddress());
        assertEquals(LocalDate.of(2000, 2, 2), saved.getDateOfBirth());
        verify(traineeDao).save(existing);
    }
}
