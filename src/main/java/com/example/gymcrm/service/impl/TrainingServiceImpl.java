package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingDao trainingDao;
    private TrainingTypeDao trainingTypeDao;

    @Autowired public void setTrainingDao(TrainingDao trainingDao) { this.trainingDao = trainingDao; }
    @Autowired public void setTrainingTypeDao(TrainingTypeDao trainingTypeDao) { this.trainingTypeDao = trainingTypeDao; }

    @Override
    public Training create(Training training) {
        if (training.getTrainingType() != null &&
                trainingTypeDao.findByName(training.getTrainingType()).isEmpty()) {
            throw new NotFoundException("Training type not found: " + training.getTrainingType());
        }
        Training saved = trainingDao.save(training);
        log.info("Created training id={} name={}", saved.getId(), saved.getTrainingName());
        return saved;
    }

    @Override public Optional<Training> getById(Long id) { return trainingDao.findById(id); }
    @Override public List<Training> list() { return new ArrayList<>(trainingDao.findAll()); }
}
