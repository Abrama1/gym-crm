// src/main/java/com/example/gymcrm/service/impl/TrainingServiceImpl.java
package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.TrainingService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingDao trainingDao;
    private final TrainingTypeDao trainingTypeDao;

    public TrainingServiceImpl(TrainingDao trainingDao, TrainingTypeDao trainingTypeDao) {
        this.trainingDao = trainingDao;
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    public Training create(Training training) {
        if (training.getTrainingType() == null ||
                trainingTypeDao.findByName(training.getTrainingType().getName()).isEmpty()) {
            throw new NotFoundException("Training type not found");
        }
        Training saved = trainingDao.save(training);
        log.info("Created training id={} name={}", saved.getId(), saved.getTrainingName());
        return saved;
    }

    @Override @Transactional(readOnly = true)
    public Optional<Training> getById(Long id){ return trainingDao.findById(id); }

    @Override @Transactional(readOnly = true)
    public List<Training> list(){ return new ArrayList<>(trainingDao.findAll()); }
}
