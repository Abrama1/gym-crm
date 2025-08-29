package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.config.TestJpaConfig;
import com.example.gymcrm.dao.*;
import com.example.gymcrm.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestJpaConfig.class)
@Transactional
class JpaTrainingDaoCrudIntegrationTest {

    @Autowired private TrainingDao trainingDao;
    @Autowired private TraineeDao traineeDao;
    @Autowired private TrainerDao trainerDao;
    @Autowired private UserDao userDao;
    @Autowired private TrainingTypeDao typeDao;

    @Test
    void save_findById_findAll() {
        User u1 = u("John","Smith","John.Smith");
        User u2 = u("Jane","Doe","Jane.Doe");
        userDao.save(u1); userDao.save(u2);

        Trainee t = new Trainee(); t.setUser(u1); t.setAddress("City");
        t.setDateOfBirth(LocalDate.of(2000,1,1)); t = traineeDao.save(t);

        Trainer r = new Trainer(); r.setUser(u2); r.setSpecialization("Strength");
        r = trainerDao.save(r);

        TrainingType tt = new TrainingType(); tt.setName("Cardio"); tt = typeDao.save(tt);

        Training tr = new Training();
        tr.setTrainee(t); tr.setTrainer(r); tr.setTrainingType(tt);
        tr.setTrainingName("Morning Run"); tr.setDurationMinutes(40);
        tr.setTrainingDate(LocalDateTime.now()); tr = trainingDao.save(tr);

        assertTrue(trainingDao.findById(tr.getId()).isPresent());
        assertEquals(1, trainingDao.findAll().size());
    }

    private User u(String f, String l, String un){
        User u = new User(); u.setFirstName(f); u.setLastName(l); u.setUsername(un); u.setPassword("pw"); u.setActive(true);
        return u;
    }
}
