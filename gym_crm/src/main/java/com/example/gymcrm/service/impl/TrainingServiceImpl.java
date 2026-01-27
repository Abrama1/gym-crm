package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.dto.WorkloadEventRequest;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.integration.WorkloadEventPublisher;
import com.example.gymcrm.service.TrainingService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingDao trainingDao;
    private final TrainingTypeDao trainingTypeDao;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    private final WorkloadEventPublisher workloadPublisher;

    private final Counter trainingCreatedCounter;
    private final Timer listForTraineeTimer;
    private final Timer listForTrainerTimer;

    public TrainingServiceImpl(TrainingDao trainingDao,
                               TrainingTypeDao trainingTypeDao,
                               TraineeDao traineeDao,
                               TrainerDao trainerDao,
                               MeterRegistry registry,
                               WorkloadEventPublisher workloadPublisher) {
        this.trainingDao = trainingDao;
        this.trainingTypeDao = trainingTypeDao;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.workloadPublisher = workloadPublisher;

        this.trainingCreatedCounter = registry.counter("gymcrm.trainings.created");
        this.listForTraineeTimer   = registry.timer("gymcrm.trainings.list", "side", "trainee");
        this.listForTrainerTimer   = registry.timer("gymcrm.trainings.list", "side", "trainer");
    }

    @Override
    public Training create(Training training) {
        String typeName = training.getTrainingType() != null ? training.getTrainingType().getName() : null;
        if (typeName == null || trainingTypeDao.findByName(typeName).isEmpty())
            throw new NotFoundException("Training type not found: " + typeName);

        if (training.getTrainee() == null || training.getTrainee().getId() == null)
            throw new NotFoundException("Trainee reference required");
        if (training.getTrainer() == null || training.getTrainer().getId() == null)
            throw new NotFoundException("Trainer reference required");

        Trainee tt = traineeDao.findById(training.getTrainee().getId())
                .orElseThrow(() -> new NotFoundException("Trainee not found"));
        Trainer trn = trainerDao.findById(training.getTrainer().getId())
                .orElseThrow(() -> new NotFoundException("Trainer not found"));
        TrainingType ttype = trainingTypeDao.findByName(typeName)
                .orElseThrow(() -> new NotFoundException("Training type not found"));

        training.setTrainee(tt);
        training.setTrainer(trn);
        training.setTrainingType(ttype);

        Training saved = trainingDao.save(training);
        trainingCreatedCounter.increment();
        log.info("Created training id={} name={}", saved.getId(), saved.getTrainingName());

        // Async publish to workload-service (ADD)
        WorkloadEventRequest ev = new WorkloadEventRequest();
        ev.setTrainerUsername(trn.getUser().getUsername());
        ev.setTrainerFirstName(trn.getUser().getFirstName());
        ev.setTrainerLastName(trn.getUser().getLastName());
        ev.setActive(trn.getUser().isActive());
        ev.setTrainingDate(saved.getTrainingDate().toLocalDate());
        ev.setTrainingDurationMinutes(saved.getDurationMinutes());
        ev.setActionType("ADD");

        // Do not break business flow if broker is down
        try {
            workloadPublisher.publish(ev);
        } catch (Exception ex) {
            log.warn("Failed to publish workload event; training is still created. reason={}", ex.toString());
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> getById(Long id){ return trainingDao.findById(id); }

    @Override
    @Transactional(readOnly = true)
    public List<Training> list(){ return new ArrayList<>(trainingDao.findAll()); }

    @Override
    @Transactional(readOnly = true)
    public List<Training> listForTrainee(String traineeUsername, TrainingCriteria c) {
        String nameLike = (c.getOtherPartyNameLike() == null) ? null : c.getOtherPartyNameLike().toLowerCase();
        return listForTraineeTimer.record(() ->
                new ArrayList<>(trainingDao.listForTrainee(
                        traineeUsername, c.getFrom(), c.getTo(), nameLike, c.getTrainingType()
                ))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> listForTrainer(String trainerUsername, TrainingCriteria c) {
        String nameLike = (c.getOtherPartyNameLike() == null) ? null : c.getOtherPartyNameLike().toLowerCase();
        return listForTrainerTimer.record(() ->
                new ArrayList<>(trainingDao.listForTrainer(
                        trainerUsername, c.getFrom(), c.getTo(), nameLike, c.getTrainingType()
                ))
        );
    }
}
