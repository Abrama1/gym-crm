package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.User;
import com.example.gymcrm.exceptions.NotFoundException;
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

class TraineeServiceImplTest {

    @Mock TraineeDao traineeDao;
    @Mock UserDao userDao;
    @Mock UsernameGenerator usernameGen;
    @Mock PasswordGenerator passwordGen;

    @InjectMocks TraineeServiceImpl service;

    @BeforeEach
    void setUp(){ MockitoAnnotations.openMocks(this); }

    @Test
    void create_generatesUserAndSavesTrainee() {
        when(usernameGen.generateUnique("John","Smith")).thenReturn("John.Smith");
        when(passwordGen.random10()).thenReturn("XXXXXXXXXX");

        when(userDao.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(traineeDao.save(any())).thenAnswer(inv -> {
            Trainee t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        Trainee t = new Trainee();
        t.setDateOfBirth(LocalDate.now());
        Trainee result = service.create(t, "John","Smith", true);

        assertEquals(10L, result.getId());
        verify(userDao).save(any(User.class));
        verify(traineeDao).save(any(Trainee.class));
    }

    @Test
    void update_notFound_throws() {
        when(traineeDao.findById(99L)).thenReturn(Optional.empty());
        Trainee t = new Trainee(); t.setId(99L);
        assertThrows(NotFoundException.class, () -> service.update(t));
    }

    @Test
    void update_success() {
        Trainee t = new Trainee(); t.setId(5L); t.setAddress("A");
        when(traineeDao.findById(5L)).thenReturn(Optional.of(t));
        when(traineeDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Trainee res = service.update(t);
        assertEquals(5L, res.getId());
        verify(traineeDao).save(t);
    }

    @Test
    void delete_success_removesTraineeAndUser() {
        Trainee t = new Trainee(); t.setId(7L); t.setUserId(100L);
        when(traineeDao.findById(7L)).thenReturn(Optional.of(t));

        service.delete(7L);

        verify(traineeDao).deleteById(7L);
        verify(userDao).deleteById(100L);
    }

    @Test
    void delete_notFound_throws() {
        when(traineeDao.findById(9L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.delete(9L));
    }

    @Test
    void getById_and_list_delegateToDao() {
        Trainee entity = new Trainee(); entity.setId(1L);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(entity));
        when(traineeDao.findAll()).thenReturn(List.of(entity));

        assertTrue(service.getById(1L).isPresent());
        assertEquals(1, service.list().size());
    }
}
