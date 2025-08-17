package com.example.gymcrm.dao.impl;

import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.domain.TrainingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryTrainingTypeDao implements TrainingTypeDao {

    @Autowired @Qualifier("trainingTypeStorage")
    private Map<String, TrainingType> storage;

    @Override public Optional<TrainingType> findByName(String name) { return Optional.ofNullable(storage.get(name)); }
    @Override public Collection<TrainingType> findAll() { return storage.values(); }
    @Override public TrainingType save(TrainingType t) { storage.put(t.getName(), t); return t; }
}
