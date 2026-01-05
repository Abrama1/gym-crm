package com.example.gymcrm.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrainingPojoTest {
    @Test
    void gettersSettersAndRelations() {
        Trainee trainee = new Trainee(); trainee.setUser(new User()); trainee.getUser().setUsername("John.Smith");
        Trainer trainer = new Trainer(); trainer.setUser(new User()); trainer.getUser().setUsername("Jane.Doe");
        TrainingType type = new TrainingType(); type.setName("Cardio");

        Training tr = new Training();
        tr.setId(5L);
        tr.setTrainee(trainee);
        tr.setTrainer(trainer);
        tr.setTrainingType(type);
        tr.setTrainingName("Morning Run");
        tr.setTrainingDate(LocalDateTime.of(2024,1,1,8,0));
        tr.setDurationMinutes(40);

        assertEquals(5L, tr.getId());
        assertEquals("Morning Run", tr.getTrainingName());
        assertEquals(LocalDateTime.of(2024,1,1,8,0), tr.getTrainingDate());
        assertEquals(40, tr.getDurationMinutes());
        assertSame(trainee, tr.getTrainee());
        assertSame(trainer, tr.getTrainer());
        assertSame(type, tr.getTrainingType());
    }
}
