package com.example.gymcrm.api;

import com.example.gymcrm.dto.*;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public TraineeController(TraineeService traineeService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    private Credentials creds(HttpServletRequest req) {
        return new Credentials(req.getHeader("X-Username"), req.getHeader("X-Password"));
    }

    @ApiOperation("Register a new trainee")
    @PostMapping("/register")
    public RegistrationResponse register(@RequestBody @Valid TraineeRegistrationRequest body) {
        Trainee trainee = new Trainee();
        trainee.setAddress(body.getAddress());
        trainee.setDateOfBirth(body.getDateOfBirth());

        Trainee saved = traineeService.create(
                trainee,
                body.getFirstName(),
                body.getLastName(),
                true
        );

        // IMPORTANT: return the generated (plain) password, not the hashed one
        return new RegistrationResponse(
                saved.getUser().getUsername(),
                saved.getUser().getPlainPassword()
        );
    }

    @ApiOperation("Get trainee profile by username")
    @GetMapping("/{username}")
    public TraineeProfileResponse getProfile(@PathVariable String username, HttpServletRequest req) {
        Trainee t = traineeService.getByUsername(creds(req), username);
        TraineeProfileResponse res = new TraineeProfileResponse();
        res.setUsername(t.getUser().getUsername());
        res.setFirstName(t.getUser().getFirstName());
        res.setLastName(t.getUser().getLastName());
        res.setDateOfBirth(t.getDateOfBirth());
        res.setAddress(t.getAddress());
        res.setActive(t.getUser().isActive());
        res.setTrainers(
                t.getTrainers().stream()
                        .map(this::toInnerTrainerSummary)
                        .collect(Collectors.toList())
        );
        return res;
    }

    @ApiOperation("Update trainee profile")
    @PutMapping("/{username}")
    public TraineeProfileResponse update(@PathVariable String username,
                                         @RequestBody @Valid UpdateTraineeRequest body,
                                         HttpServletRequest req) {
        // enforce self
        Trainee current = traineeService.getByUsername(creds(req), username);

        // patch allowed fields on Trainee
        Trainee patch = new Trainee();
        patch.setAddress(body.getAddress());
        patch.setDateOfBirth(body.getDateOfBirth());
        Trainee updated = traineeService.updateProfile(creds(req), patch);

        // propagate first/last/active on the joined User
        if (body.getFirstName() != null) current.getUser().setFirstName(body.getFirstName());
        if (body.getLastName() != null) current.getUser().setLastName(body.getLastName());
        if (body.getActive() != null) current.getUser().setActive(body.getActive());
        traineeService.update(current);

        TraineeProfileResponse res = new TraineeProfileResponse();
        res.setUsername(updated.getUser().getUsername());
        res.setFirstName(current.getUser().getFirstName());
        res.setLastName(current.getUser().getLastName());
        res.setDateOfBirth(updated.getDateOfBirth());
        res.setAddress(updated.getAddress());
        res.setActive(current.getUser().isActive());
        res.setTrainers(
                updated.getTrainers().stream()
                        .map(this::toInnerTrainerSummary)
                        .collect(Collectors.toList())
        );
        return res;
    }

    @ApiOperation("Delete trainee profile")
    @DeleteMapping("/{username}")
    public void delete(@PathVariable String username, HttpServletRequest req) {
        traineeService.deleteByUsername(creds(req), username);
    }

    @ApiOperation("Get not-assigned active trainers for a trainee")
    @GetMapping("/{username}/trainers/available")
    public TrainersListResponse availableTrainers(@PathVariable String username, HttpServletRequest req) {
        List<Trainer> list = trainerService.listNotAssignedToTrainee(creds(req), username);
        TrainersListResponse res = new TrainersListResponse();
        res.setItems(list.stream().map(this::toTopLevelTrainerSummary).collect(Collectors.toList()));
        return res;
    }

    @ApiOperation("Replace trainee's trainer list")
    @PutMapping("/{username}/trainers")
    public TrainersListResponse setTrainers(@PathVariable String username,
                                            @RequestBody @Valid UpdateTraineeTrainersRequest body,
                                            HttpServletRequest req) {
        traineeService.setTrainers(creds(req), username, body.getTrainers());
        Trainee t = traineeService.getByUsername(creds(req), username);

        TrainersListResponse res = new TrainersListResponse();
        res.setItems(t.getTrainers().stream().map(this::toTopLevelTrainerSummary).collect(Collectors.toList()));
        return res;
    }

    // Mappers
    /** Builds the inner type required by TraineeProfileResponse. */
    private TraineeProfileResponse.TrainerSummary toInnerTrainerSummary(Trainer tr) {
        TraineeProfileResponse.TrainerSummary s = new TraineeProfileResponse.TrainerSummary();
        s.setUsername(tr.getUser().getUsername());
        s.setFirstName(tr.getUser().getFirstName());
        s.setLastName(tr.getUser().getLastName());
        s.setSpecialization(resolveSpecializationText(tr));
        return s;
    }

    /** Builds the top-level TrainerSummary (for TrainersListResponse). */
    private TrainerSummary toTopLevelTrainerSummary(Trainer tr) {
        TrainerSummary s = new TrainerSummary();
        s.setUsername(tr.getUser().getUsername());
        s.setFirstName(tr.getUser().getFirstName());
        s.setLastName(tr.getUser().getLastName());
        s.setSpecialization(resolveSpecializationText(tr));
        return s;
    }

    /**
     * Works whether Trainer#specialization is a String or an entity (e.g., TrainingType with getName()).
     */
    private String resolveSpecializationText(Trainer tr) {
        Object spec = tr.getSpecialization();
        if (spec == null) return null;
        try {
            var m = spec.getClass().getMethod("getName");
            Object v = m.invoke(spec);
            return v != null ? v.toString() : null;
        } catch (Exception ignore) {
            return spec.toString();
        }
    }
}
