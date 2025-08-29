package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TraineeServiceImplCrudNegativeTest {

    @Mock TraineeDao traineeDao;
    @Mock TrainerDao trainerDao;
    @Mock UserDao userDao;
    @Mock UsernameGenerator usernameGen;
    @Mock PasswordGenerator passwordGen;
    @Mock AuthService authService;

    TraineeService service;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        service = new TraineeServiceImpl(traineeDao, trainerDao, userDao, usernameGen, passwordGen, authService);
    }

    @Test
    void update_notFound_throws() {
        Trainee t = new Trainee(); t.setId(99L);
        when(traineeDao.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.update(t));
    }

    @Test
    void delete_notFound_throws() {
        when(traineeDao.findById(77L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.delete(77L));
    }
}
