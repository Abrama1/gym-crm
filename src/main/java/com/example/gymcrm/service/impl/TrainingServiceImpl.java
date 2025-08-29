package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.*;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.AuthService;
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
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final AuthService authService;

    public TrainingServiceImpl(TrainingDao trainingDao, TrainingTypeDao trainingTypeDao,
                               TraineeDao traineeDao, TrainerDao trainerDao, AuthService authService) {
        this.trainingDao = trainingDao;
        this.trainingTypeDao = trainingTypeDao;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.authService = authService;
    }

    @Override
    public Training create(Training training) {
        // validate type exists by name
        String typeName = training.getTrainingType() != null ? training.getTrainingType().getName() : null;
        if (typeName == null || trainingTypeDao.findByName(typeName).isEmpty())
            throw new NotFoundException("Training type not found: " + typeName);

        // ensure trainee & trainer are managed references
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
        log.info("Created training id={} name={}", saved.getId(), saved.getTrainingName());
        return saved;
    }

    @Override @Transactional(readOnly = true)
    public Optional<Training> getById(Long id){ return trainingDao.findById(id); }

    @Override @Transactional(readOnly = true)
    public List<Training> list(){ return new ArrayList<>(trainingDao.findAll()); }

    // criteria views (auth: user can only view their own trainings)

    @Override @Transactional(readOnly = true)
    public List<Training> listForTrainee(Credentials auth, String traineeUsername, TrainingCriteria c) {
        var me = authService.authenticateTrainee(auth);
        if (!me.getUser().getUsername().equalsIgnoreCase(traineeUsername))
            throw new com.example.gymcrm.exceptions.AuthFailedException("Access denied to other trainee trainings");

        String nameLike = (c.getOtherPartyNameLike() == null) ? null : c.getOtherPartyNameLike().toLowerCase();
        return new ArrayList<>(trainingDao.listForTrainee(
                traineeUsername,
                c.getFrom(), c.getTo(),
                nameLike, c.getTrainingType()
        ));
    }

    @Override @Transactional(readOnly = true)
    public List<Training> listForTrainer(Credentials auth, String trainerUsername, TrainingCriteria c) {
        var me = authService.authenticateTrainer(auth);
        if (!me.getUser().getUsername().equalsIgnoreCase(trainerUsername))
            throw new com.example.gymcrm.exceptions.AuthFailedException("Access denied to other trainer trainings");

        String nameLike = (c.getOtherPartyNameLike() == null) ? null : c.getOtherPartyNameLike().toLowerCase();
        return new ArrayList<>(trainingDao.listForTrainer(
                trainerUsername,
                c.getFrom(), c.getTo(),
                nameLike, c.getTrainingType()
        ));
    }
}
