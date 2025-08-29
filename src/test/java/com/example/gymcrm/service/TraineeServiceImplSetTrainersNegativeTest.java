package com.example.gymcrm.service;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import com.example.gymcrm.util.PasswordGenerator;
import com.example.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TraineeServiceImplSetTrainersNegativeTest {

    @Mock TraineeDao traineeDao;
    @Mock TrainerDao trainerDao;
    @Mock UserDao userDao;
    @Mock UsernameGenerator usernameGen;
    @Mock PasswordGenerator passwordGen;
    @Mock AuthService authService;

    TraineeService service;
    private Trainee me;
    private Credentials creds;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new TraineeServiceImpl(traineeDao, trainerDao, userDao, usernameGen, passwordGen, authService);

        User u = new User();
        u.setUsername("John.Smith");
        u.setActive(true);
        u.setPassword("pw");

        me = new Trainee();
        me.setUser(u);
        me.setAddress("A");
        me.setDateOfBirth(LocalDate.of(2000,1,1));

        creds = new Credentials("John.Smith","pw");
        when(authService.authenticateTrainee(creds)).thenReturn(me);
    }

    @Test
    void setTrainers_unknownTrainer_throws() {
        when(trainerDao.findByUsername("Missing.Trainer")).thenReturn(java.util.Optional.empty());
        assertThrows(NotFoundException.class, () ->
                service.setTrainers(creds, "John.Smith", List.of("Missing.Trainer")));
        verify(trainerDao).findByUsername("Missing.Trainer");
        verifyNoMoreInteractions(trainerDao);
    }
}
