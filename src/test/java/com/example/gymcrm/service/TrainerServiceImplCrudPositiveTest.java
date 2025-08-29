package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplCrudPositiveTest {

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
        when(usernameGen.generateUnique(anyString(), anyString())).thenReturn("Jane.Doe");
        when(passwordGen.random10()).thenReturn("secret10");
        when(userDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(trainerDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void create_ok_generatesUser() {
        Trainer tr = new Trainer(); tr.setSpecialization("Strength");
        Trainer saved = service.create(tr, "Jane", "Doe", true);
        assertNotNull(saved.getUser());
        assertEquals("Jane.Doe", saved.getUser().getUsername());
        verify(userDao).save(any(User.class));
        verify(trainerDao).save(tr);
    }

    @Test
    void update_ok() {
        Trainer tr = new Trainer(); tr.setId(7L); tr.setSpecialization("Cardio");
        when(trainerDao.findById(7L)).thenReturn(Optional.of(tr));
        when(trainerDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Trainer saved = service.update(tr);
        assertEquals("Cardio", saved.getSpecialization());
        verify(trainerDao).save(tr);
    }

    @Test
    void getById_and_list() {
        Trainer x = new Trainer(); x.setId(1L);
        when(trainerDao.findById(1L)).thenReturn(Optional.of(x));
        when(trainerDao.findAll()).thenReturn(List.of(x));
        assertTrue(service.getById(1L).isPresent());
        assertEquals(1, service.list().size());
    }
}
