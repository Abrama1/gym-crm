package com.example.gymcrm.dao.impl;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.domain.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTraineeDao implements TraineeDao {

    @Autowired @Qualifier("traineeStorage")
    private Map<Long, Trainee> storage;

    private final AtomicLong seq = new AtomicLong(0);

    @Override public Optional<Trainee> findById(Long id) { return Optional.ofNullable(storage.get(id)); }
    @Override public Collection<Trainee> findAll() { return storage.values(); }
    @Override public Trainee save(Trainee trainee) {
        if (trainee.getId() == null) trainee.setId(seq.incrementAndGet());
        storage.put(trainee.getId(), trainee);
        return trainee;
    }
    @Override public void deleteById(Long id) { storage.remove(id); }
}
