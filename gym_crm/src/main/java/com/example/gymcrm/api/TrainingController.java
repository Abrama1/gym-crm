package com.example.gymcrm.api;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dto.*;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.TrainingService;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    public TrainingController(TrainingService trainingService, TraineeDao traineeDao, TrainerDao trainerDao) {
        this.trainingService = trainingService;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    @ApiOperation("Create a training")
    @PostMapping
    @PreAuthorize("(hasRole('TRAINEE') and #body.traineeUsername == authentication.name) or " +
            "(hasRole('TRAINER') and #body.trainerUsername == authentication.name)")
    public void create(@RequestBody @Valid AddTrainingRequest body) {
        Trainee trainee = traineeDao.findByUsername(body.getTraineeUsername())
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + body.getTraineeUsername()));
        Trainer trainer = trainerDao.findByUsername(body.getTrainerUsername())
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + body.getTrainerUsername()));

        Training t = new Training();
        t.setTrainingName(body.getTrainingName());
        t.setTrainingDate(body.getTrainingDate().atStartOfDay());
        t.setDurationMinutes(body.getTrainingDuration());
        TrainingType type = new TrainingType();
        type.setName(body.getTrainingType());
        t.setTrainingType(type);
        t.setTrainee(trainee);
        t.setTrainer(trainer);

        trainingService.create(t);
    }

    @ApiOperation("List trainings for trainee")
    @GetMapping("/trainee/{username}")
    @PreAuthorize("hasRole('TRAINEE') and #username == authentication.name")
    public List<TrainingItemResponse> listForTrainee(@PathVariable String username,
                                                     @RequestParam(required = false) String trainingType,
                                                     @RequestParam(required = false) String otherPartyLike,
                                                     @RequestParam(required = false) String from,
                                                     @RequestParam(required = false) String to) {
        TrainingCriteria c = new TrainingCriteria(
                from != null ? LocalDateTime.parse(from) : null,
                to != null ? LocalDateTime.parse(to) : null,
                trainingType,
                otherPartyLike
        );
        return trainingService.listForTrainee(username, c)
                .stream()
                .map(tr -> {
                    TrainingItemResponse r = new TrainingItemResponse();
                    r.setTrainingName(tr.getTrainingName());
                    r.setTrainingDate(tr.getTrainingDate().toLocalDate());
                    r.setTrainingType(tr.getTrainingType().getName());
                    r.setTrainingDuration(tr.getDurationMinutes());
                    String otherName = tr.getTrainer().getUser().getFirstName() + " " + tr.getTrainer().getUser().getLastName();
                    r.setOtherPartyName(otherName);
                    return r;
                })
                .toList();
    }

    @ApiOperation("List trainings for trainer")
    @GetMapping("/trainer/{username}")
    @PreAuthorize("hasRole('TRAINER') and #username == authentication.name")
    public List<TrainingItemResponse> listForTrainer(@PathVariable String username,
                                                     @RequestParam(required = false) String trainingType,
                                                     @RequestParam(required = false) String otherPartyLike,
                                                     @RequestParam(required = false) String from,
                                                     @RequestParam(required = false) String to) {
        TrainingCriteria c = new TrainingCriteria(
                from != null ? LocalDateTime.parse(from) : null,
                to != null ? LocalDateTime.parse(to) : null,
                trainingType,
                otherPartyLike
        );
        return trainingService.listForTrainer(username, c)
                .stream()
                .map(tr -> {
                    TrainingItemResponse r = new TrainingItemResponse();
                    r.setTrainingName(tr.getTrainingName());
                    r.setTrainingDate(tr.getTrainingDate().toLocalDate());
                    r.setTrainingType(tr.getTrainingType().getName());
                    r.setTrainingDuration(tr.getDurationMinutes());
                    String otherName = tr.getTrainee().getUser().getFirstName() + " " + tr.getTrainee().getUser().getLastName();
                    r.setOtherPartyName(otherName);
                    return r;
                })
                .toList();
    }
}
