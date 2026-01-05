package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.config.TestJpaConfig;
import com.example.gymcrm.dao.TrainerDao;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestJpaConfig.class)
@Transactional
class JpaTrainerDaoIntegrationTest {

    @PersistenceContext EntityManager em;
    @Autowired private TrainerDao trainerDao;

    @Test
    void listNotAssignedToTrainee_excludesAssignedOnes() {
        // Users
        User uTr = user("John","Smith","John.Smith","pw", true);
        User uJane = user("Jane","Doe","Jane.Doe","pw", true);
        User uBob = user("Bob","Jones","Bob.Jones","pw", true);
        em.persist(uTr); em.persist(uJane); em.persist(uBob);

        // Profiles
        Trainee trainee = new Trainee(); trainee.setUser(uTr); trainee.setAddress("City"); trainee.setDateOfBirth(LocalDate.of(2000,1,1));
        Trainer jane = new Trainer(); jane.setUser(uJane); jane.setSpecialization("Strength");
        Trainer bob  = new Trainer(); bob.setUser(uBob);  bob.setSpecialization("Cardio");
        em.persist(trainee); em.persist(jane); em.persist(bob);

        // Assign Jane to trainee
        trainee.getTrainers().add(jane);
        em.merge(trainee);
        em.flush();

        var notAssigned = trainerDao.listNotAssignedToTrainee("John.Smith");
        assertTrue(notAssigned.stream().anyMatch(t -> "Cardio".equals(t.getSpecialization())));
        assertTrue(notAssigned.stream().noneMatch(t -> "Strength".equals(t.getSpecialization())));
    }

    private User user(String fn, String ln, String un, String pw, boolean active){
        User u = new User(); u.setFirstName(fn); u.setLastName(ln); u.setUsername(un); u.setPassword(pw); u.setActive(active);
        return u;
    }
}
