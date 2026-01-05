package com.example.gymcrm.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineePojoTest {
    @Test
    void gettersSettersAndRelations() {
        User u = new User(); u.setId(10L); u.setUsername("John.Smith");
        Trainee t = new Trainee();
        t.setId(3L);
        t.setUser(u);
        t.setAddress("City");
        t.setDateOfBirth(LocalDate.of(2000,1,1));

        Trainer tr = new Trainer();
        tr.setUser(new User());
        tr.getUser().setUsername("Jane.Doe");
        tr.setSpecialization("Strength");
        t.getTrainers().add(tr);

        assertEquals(3L, t.getId());
        assertEquals("City", t.getAddress());
        assertEquals(LocalDate.of(2000,1,1), t.getDateOfBirth());
        assertSame(u, t.getUser());
        assertEquals(1, t.getTrainers().size());
        assertEquals("Jane.Doe", t.getTrainers().iterator().next().getUser().getUsername());
    }
}
