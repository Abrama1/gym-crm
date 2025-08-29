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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplCrudPositiveTest {

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
        when(usernameGen.generateUnique(anyString(), anyString())).thenReturn("John.Smith");
        when(passwordGen.random10()).thenReturn("secret10");
        when(userDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(traineeDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void create_ok_generatesUser() {
        Trainee t = new Trainee();
        t.setAddress("City"); t.setDateOfBirth(LocalDate.of(2000,1,1));

        Trainee saved = service.create(t, "John", "Smith", true);

        assertNotNull(saved.getUser());
        assertEquals("John.Smith", saved.getUser().getUsername());
        verify(userDao).save(any(User.class));
        verify(traineeDao).save(t);
    }

    @Test
    void delete_ok_cascadesUserDelete() {
        Trainee existing = new Trainee();
        User u = new User(); u.setId(5L);
        existing.setUser(u); existing.setId(10L);
        when(traineeDao.findById(10L)).thenReturn(Optional.of(existing));

        service.delete(10L);

        verify(traineeDao).delete(existing);
        verify(userDao).deleteById(5L);
    }

    @Test
    void getById_and_list() {
        Trainee x = new Trainee(); x.setId(1L);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(x));
        when(traineeDao.findAll()).thenReturn(List.of(x));

        assertTrue(service.getById(1L).isPresent());
        assertEquals(1, service.list().size());
    }
}
