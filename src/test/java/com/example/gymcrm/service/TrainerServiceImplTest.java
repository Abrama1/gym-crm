package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.User;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    @Mock TrainerDao trainerDao;
    @Mock UserDao userDao;
    @Mock UsernameGenerator usernameGen;
    @Mock PasswordGenerator passwordGen;

    @InjectMocks TrainerServiceImpl service;

    @BeforeEach
    void setup() { MockitoAnnotations.openMocks(this); }

    @Test
    void create_generatesUserAndSavesTrainer() {
        when(usernameGen.generateUnique("Jane", "Doe")).thenReturn("Jane.Doe");
        when(passwordGen.random10()).thenReturn("XXXXXXXXXX");

        when(userDao.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0); u.setId(100L); return u;
        });
        when(trainerDao.save(any())).thenAnswer(inv -> {
            Trainer t = inv.getArgument(0); t.setId(200L); return t;
        });

        Trainer t = new Trainer(); t.setSpecialization("Strength");
        Trainer saved = service.create(t, "Jane", "Doe", true);

        assertEquals(200L, saved.getId());
        verify(userDao).save(any(User.class));
        verify(trainerDao).save(any(Trainer.class));
    }

    @Test
    void update_notFound_throws() {
        Trainer t = new Trainer(); t.setId(999L);
        when(trainerDao.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.update(t));
    }

    @Test
    void update_success() {
        Trainer t = new Trainer(); t.setId(2L); t.setSpecialization("Strength");
        when(trainerDao.findById(2L)).thenReturn(Optional.of(t));
        when(trainerDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Trainer res = service.update(t);
        assertEquals(2L, res.getId());
        verify(trainerDao).save(t);
    }

    @Test
    void getById_and_list_delegateToDao() {
        Trainer entity = new Trainer(); entity.setId(1L);

        when(trainerDao.findById(1L)).thenReturn(Optional.of(entity));
        assertTrue(service.getById(1L).isPresent());

        when(trainerDao.findAll()).thenReturn(java.util.List.of(entity));
        assertEquals(1, service.list().size());
    }
}
