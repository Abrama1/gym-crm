package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.config.TestJpaConfig;
import com.example.gymcrm.dao.*;
import com.example.gymcrm.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestJpaConfig.class)
@Transactional
class JpaTrainingDaoIntegrationTest {

    @PersistenceContext EntityManager em;

    @Autowired TrainingDao trainingDao;
    @Autowired TrainingTypeDao trainingTypeDao;
    @Autowired TraineeDao traineeDao;
    @Autowired TrainerDao trainerDao;

    @Test
    void listForTrainee_and_listForTrainer_applyFilters() {
        // Users
        User uTrainee = user("John","Smith","John.Smith", true, "p1");
        User uTrainer = user("Jane","Doe","Jane.Doe", true, "p2");
        em.persist(uTrainee); em.persist(uTrainer);

        // Profiles
        Trainee trainee = new Trainee(); trainee.setUser(uTrainee); trainee.setAddress("Tbilisi"); trainee.setDateOfBirth(LocalDate.of(2003,5,2));
        Trainer trainer = new Trainer(); trainer.setUser(uTrainer); trainer.setSpecialization("Strength");
        em.persist(trainee); em.persist(trainer);

        // Type
        TrainingType cardio = new TrainingType(); cardio.setName("Cardio"); em.persist(cardio);

        // Training
        Training t = new Training();
        t.setTrainee(trainee); t.setTrainer(trainer); t.setTrainingType(cardio);
        t.setTrainingName("Morning Run");
        t.setTrainingDate(LocalDateTime.now().minusDays(1));
        t.setDurationMinutes(40);
        em.persist(t);
        em.flush();

        var from = LocalDateTime.now().minusDays(7);
        var to   = LocalDateTime.now().plusDays(1);

        // Trainee view (filter by trainer full name like and type)
        var byTrainee = trainingDao.listForTrainee("John.Smith", from, to, "%jane doe%", "Cardio");
        assertEquals(1, byTrainee.size());

        // Trainer view (filter by trainee full name like and type)
        var byTrainer = trainingDao.listForTrainer("Jane.Doe", from, to, "%john smith%", "Cardio");
        assertEquals(1, byTrainer.size());
    }

    private User user(String fn, String ln, String un, boolean active, String pw){
        User u = new User();
        u.setFirstName(fn); u.setLastName(ln); u.setUsername(un); u.setActive(active); u.setPassword(pw);
        return u;
    }
}
