package com.example.gymcrm.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrainerPojoTest {
    @Test
    void gettersSettersAndRelations() {
        User u = new User(); u.setId(20L); u.setUsername("Jane.Doe");
        Trainer tr = new Trainer();
        tr.setId(4L);
        tr.setUser(u);
        tr.setSpecialization("Cardio");

        Trainee trainee = new Trainee();
        trainee.setUser(new User());
        trainee.getUser().setUsername("John.Smith");
        tr.getTrainees().add(trainee);

        assertEquals(4L, tr.getId());
        assertSame(u, tr.getUser());
        assertEquals("Cardio", tr.getSpecialization());
        assertEquals(1, tr.getTrainees().size());
        assertEquals("John.Smith", tr.getTrainees().iterator().next().getUser().getUsername());
    }
}
