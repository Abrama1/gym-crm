package com.example.gymcrm.dao.impl;

import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.domain.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTrainingDao implements TrainingDao {

    @Autowired @Qualifier("trainingStorage")
    private Map<Long, Training> storage;

    private final AtomicLong seq = new AtomicLong(0);

    @Override public Optional<Training> findById(Long id) { return Optional.ofNullable(storage.get(id)); }
    @Override public Collection<Training> findAll() { return storage.values(); }
    @Override public Training save(Training training) {
        if (training.getId() == null) training.setId(seq.incrementAndGet());
        storage.put(training.getId(), training);
        return training;
    }
}
