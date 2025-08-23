package com.example.gymcrm.dao.impl;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.domain.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTrainerDao implements TrainerDao {

    @Autowired @Qualifier("trainerStorage")
    private Map<Long, Trainer> storage;

    private final AtomicLong seq = new AtomicLong(0);

    @Override public Optional<Trainer> findById(Long id) { return Optional.ofNullable(storage.get(id)); }
    @Override public Collection<Trainer> findAll() { return storage.values(); }
    @Override public Trainer save(Trainer trainer) {
        if (trainer.getId() == null) trainer.setId(seq.incrementAndGet());
        storage.put(trainer.getId(), trainer);
        return trainer;
    }
}
