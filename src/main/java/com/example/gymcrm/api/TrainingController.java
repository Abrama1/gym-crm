package com.example.gymcrm.api;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dto.AddTrainingRequest;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.dto.TrainingItemResponse;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.TrainingService;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
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

    private static Credentials creds(HttpServletRequest req) {
        return new Credentials(req.getHeader("X-Username"), req.getHeader("X-Password"));
    }

    // ---------- Add training ----------

    @ApiOperation("Create a training")
    @PostMapping("/trainings")
    public TrainingItemResponse add(@RequestBody @Valid AddTrainingRequest body,
                                    HttpServletRequest req) {
        // Resolve trainee & trainer by username (controller-level)
        Trainee trainee = traineeDao.findByUsername(body.getTraineeUsername())
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + body.getTraineeUsername()));
        Trainer trainer = trainerDao.findByUsername(body.getTrainerUsername())
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + body.getTrainerUsername()));

        // Build Training entity for service
        Training t = new Training();
        t.setTrainingName(body.getTrainingName());
        t.setTrainingDate(body.getTrainingDate().atStartOfDay());
        t.setDurationMinutes(body.getTrainingDuration());

        Trainee tRef = new Trainee(); tRef.setId(trainee.getId());
        Trainer rRef = new Trainer(); rRef.setId(trainer.getId());
        t.setTrainee(tRef);
        t.setTrainer(rRef);

        TrainingType type = new TrainingType();
        type.setName(body.getTrainingType());
        t.setTrainingType(type);

        Training saved = trainingService.create(t);

        // Map to response from the “trainer/trainee” perspective isn’t defined,
        // so return a neutral item with “otherPartyName” set to trainer’s name.
        TrainingItemResponse res = new TrainingItemResponse();
        res.setTrainingName(saved.getTrainingName());
        res.setTrainingDate(saved.getTrainingDate().toLocalDate());
        res.setTrainingDuration(saved.getDurationMinutes());
        res.setTrainingType(saved.getTrainingType() != null ? saved.getTrainingType().getName() : null);
        String trainerFull = fullName(trainer.getUser().getFirstName(), trainer.getUser().getLastName());
        res.setOtherPartyName(trainerFull);
        return res;
    }

    // ---------- Lists for trainee/trainer ----------

    @ApiOperation("List trainings for a trainee (self)")
    @GetMapping("/trainees/{username}/trainings")
    public List<TrainingItemResponse> traineeTrainings(@PathVariable String username,
                                                       @RequestParam(required = false) String from,
                                                       @RequestParam(required = false) String to,
                                                       @RequestParam(required = false, name = "trainingType") String type,
                                                       @RequestParam(required = false, name = "nameLike") String otherPartyNameLike,
                                                       HttpServletRequest req) {

        TrainingCriteria c = new TrainingCriteria(
                parseDateTime(from),
                parseDateTime(to),
                emptyToNull(type),
                emptyToNull(otherPartyNameLike)
        );

        List<Training> list = trainingService.listForTrainee(creds(req), username, c);
        return list.stream().map(tr -> {
            TrainingItemResponse r = new TrainingItemResponse();
            r.setTrainingName(tr.getTrainingName());
            r.setTrainingDate(tr.getTrainingDate() != null ? tr.getTrainingDate().toLocalDate() : null);
            r.setTrainingType(tr.getTrainingType() != null ? tr.getTrainingType().getName() : null);
            r.setTrainingDuration(tr.getDurationMinutes());
            String trainerFull = tr.getTrainer() != null && tr.getTrainer().getUser() != null
                    ? fullName(tr.getTrainer().getUser().getFirstName(), tr.getTrainer().getUser().getLastName())
                    : null;
            r.setOtherPartyName(trainerFull);
            return r;
        }).collect(Collectors.toList());
    }

    @ApiOperation("List trainings for a trainer (self)")
    @GetMapping("/trainers/{username}/trainings")
    public List<TrainingItemResponse> trainerTrainings(@PathVariable String username,
                                                       @RequestParam(required = false) String from,
                                                       @RequestParam(required = false) String to,
                                                       @RequestParam(required = false, name = "trainingType") String type,
                                                       @RequestParam(required = false, name = "nameLike") String otherPartyNameLike,
                                                       HttpServletRequest req) {

        TrainingCriteria c = new TrainingCriteria(
                parseDateTime(from),
                parseDateTime(to),
                emptyToNull(type),
                emptyToNull(otherPartyNameLike)
        );

        List<Training> list = trainingService.listForTrainer(creds(req), username, c);
        return list.stream().map(tr -> {
            TrainingItemResponse r = new TrainingItemResponse();
            r.setTrainingName(tr.getTrainingName());
            r.setTrainingDate(tr.getTrainingDate() != null ? tr.getTrainingDate().toLocalDate() : null);
            r.setTrainingType(tr.getTrainingType() != null ? tr.getTrainingType().getName() : null);
            r.setTrainingDuration(tr.getDurationMinutes());
            String traineeFull = tr.getTrainee() != null && tr.getTrainee().getUser() != null
                    ? fullName(tr.getTrainee().getUser().getFirstName(), tr.getTrainee().getUser().getLastName())
                    : null;
            r.setOtherPartyName(traineeFull);
            return r;
        }).collect(Collectors.toList());
    }

    // ---------- helpers ----------

    private static LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();
        if (t.contains("T")) {
            return LocalDateTime.parse(t);
        }
        // Just a date -> start of day
        LocalDate d = LocalDate.parse(t);
        return d.atStartOfDay();
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static String fullName(String first, String last) {
        String f = first == null ? "" : first.trim();
        String l = last == null ? "" : last.trim();
        return (f + " " + l).trim().replaceAll("\\s{2,}", " ");
    }
}
