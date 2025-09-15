package com.example.gymcrm.api;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dto.AddTrainingRequest;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.dto.TrainingItemResponse;
import com.example.gymcrm.dto.TrainingsResponse;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.TrainingService;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    public TrainingController(TrainingService trainingService,
                              TraineeDao traineeDao,
                              TrainerDao trainerDao) {
        this.trainingService = trainingService;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    // ---- helpers

    private Credentials creds(HttpServletRequest req) {
        return new Credentials(
                req.getHeader("X-Username"),
                req.getHeader("X-Password"));
    }

    private TrainingItemResponse toDto(Training t, boolean forTraineeView) {
        var dto = new TrainingItemResponse();
        dto.setTrainingName(t.getTrainingName());
        dto.setTrainingDate(t.getTrainingDate() != null ? t.getTrainingDate().toLocalDate() : null);
        dto.setTrainingType(t.getTrainingType() != null ? t.getTrainingType().getName() : null);
        dto.setTrainingDuration(t.getDurationMinutes());
        if (forTraineeView) {
            Trainer tr = t.getTrainer();
            String name = (tr != null && tr.getUser() != null)
                    ? tr.getUser().getFirstName() + " " + tr.getUser().getLastName()
                    : null;
            dto.setOtherPartyName(name);
        } else {
            Trainee trn = t.getTrainee();
            String name = (trn != null && trn.getUser() != null)
                    ? trn.getUser().getFirstName() + " " + trn.getUser().getLastName()
                    : null;
            dto.setOtherPartyName(name);
        }
        return dto;
    }

    private TrainingsResponse wrap(List<TrainingItemResponse> items) {
        var res = new TrainingsResponse();
        res.setItems(items);
        return res;
    }

    // ---- endpoints

    @ApiOperation("Get trainee trainings list")
    @GetMapping("/trainee/{username}")
    public TrainingsResponse traineeTrainings(@PathVariable String username,
                                              @RequestParam(required = false) LocalDate from,
                                              @RequestParam(required = false) LocalDate to,
                                              @RequestParam(name = "trainerName", required = false) String trainerName,
                                              @RequestParam(name = "trainingType", required = false) String trainingType,
                                              HttpServletRequest req) {
        var c = new TrainingCriteria();
        c.setFrom(from == null ? null : from.atStartOfDay());
        c.setTo(to == null ? null : to.atTime(23, 59, 59));
        c.setOtherPartyNameLike(trainerName);
        c.setTrainingType(trainingType);

        var list = trainingService.listForTrainee(creds(req), username, c).stream()
                .map(t -> toDto(t, true))
                .collect(Collectors.toList());
        return wrap(list);
    }

    @ApiOperation("Get trainer trainings list")
    @GetMapping("/trainer/{username}")
    public TrainingsResponse trainerTrainings(@PathVariable String username,
                                              @RequestParam(required = false) LocalDate from,
                                              @RequestParam(required = false) LocalDate to,
                                              @RequestParam(name = "traineeName", required = false) String traineeName,
                                              HttpServletRequest req) {
        var c = new TrainingCriteria();
        c.setFrom(from == null ? null : from.atStartOfDay());
        c.setTo(to == null ? null : to.atTime(23, 59, 59));
        c.setOtherPartyNameLike(traineeName);

        var list = trainingService.listForTrainer(creds(req), username, c).stream()
                .map(t -> toDto(t, false))
                .collect(Collectors.toList());
        return wrap(list);
    }

    @ApiOperation("Add training")
    @PostMapping
    public ResponseEntity<Void> add(@RequestBody @Valid AddTrainingRequest body,
                                    HttpServletRequest req) {

        // resolve trainee/trainer by username to set IDs
        var trainee = traineeDao.findByUsername(body.getTraineeUsername())
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + body.getTraineeUsername()));
        var trainer = trainerDao.findByUsername(body.getTrainerUsername())
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + body.getTrainerUsername()));

        var training = new Training();
        training.setTrainingName(body.getTrainingName());
        training.setTrainingDate(body.getTrainingDate() != null ? body.getTrainingDate().atStartOfDay() : null);
        training.setDurationMinutes(body.getTrainingDuration());

        var tt = new TrainingType();
        tt.setName(body.getTrainingType());
        training.setTrainingType(tt);

        var tnr = new Trainer();
        tnr.setId(trainer.getId());
        training.setTrainer(tnr);

        var trn = new Trainee();
        trn.setId(trainee.getId());
        training.setTrainee(trn);

        trainingService.create(training); // service validates type & references
        return ResponseEntity.ok().build();
    }
}
