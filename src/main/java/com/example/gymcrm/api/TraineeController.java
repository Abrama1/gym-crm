package com.example.gymcrm.api;

import com.example.gymcrm.dto.*;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @ApiOperation("Register a new trainee")
    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public RegistrationResponse register(@RequestBody @Valid TraineeRegistrationRequest body) {
        Trainee trainee = new Trainee();
        trainee.setAddress(body.getAddress());
        trainee.setDateOfBirth(body.getDateOfBirth());

        Trainee saved = traineeService.create(trainee, body.getFirstName(), body.getLastName(), true);
        return new RegistrationResponse(saved.getUser().getUsername(), saved.getUser().getPlainPassword());
    }

    @ApiOperation("Get trainee profile by username")
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('TRAINEE') and #username == authentication.name")
    public TraineeProfileResponse getProfile(@PathVariable String username) {
        Trainee t = traineeService.getByUsername(username);
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
    @PreAuthorize("hasRole('TRAINEE') and #username == authentication.name")
    public TraineeProfileResponse update(@PathVariable String username,
                                         @RequestBody @Valid UpdateTraineeRequest body) {

        Trainee patch = new Trainee();
        patch.setAddress(body.getAddress());
        patch.setDateOfBirth(body.getDateOfBirth());
        Trainee updated = traineeService.updateProfile(username, patch);

        // propagate nested User fields (first/last/active) through service.update()
        Trainee current = traineeService.getByUsername(username);
        if (body.getFirstName() != null) current.getUser().setFirstName(body.getFirstName());
        if (body.getLastName() != null)  current.getUser().setLastName(body.getLastName());
        if (body.getActive() != null)    current.getUser().setActive(body.getActive());
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
    @PreAuthorize("hasRole('TRAINEE') and #username == authentication.name")
    public void delete(@PathVariable String username) {
        traineeService.deleteByUsername(username);
    }

    @ApiOperation("Get not-assigned active trainers for a trainee")
    @GetMapping("/{username}/trainers/available")
    @PreAuthorize("hasRole('TRAINEE') and #username == authentication.name")
    public TrainersListResponse availableTrainers(@PathVariable String username) {
        List<Trainer> list = trainerService.listNotAssignedToTrainee(username);
        TrainersListResponse res = new TrainersListResponse();
        res.setItems(list.stream().map(this::toTopLevelTrainerSummary).collect(Collectors.toList()));
        return res;
    }

    @ApiOperation("Replace trainee's trainer list")
    @PutMapping("/{username}/trainers")
    @PreAuthorize("hasRole('TRAINEE') and #username == authentication.name")
    public TrainersListResponse setTrainers(@PathVariable String username,
                                            @RequestBody @Valid UpdateTraineeTrainersRequest body) {
        traineeService.setTrainers(username, body.getTrainers());
        Trainee t = traineeService.getByUsername(username);

        TrainersListResponse res = new TrainersListResponse();
        res.setItems(t.getTrainers().stream().map(this::toTopLevelTrainerSummary).collect(Collectors.toList()));
        return res;
    }

    // --- mappers ---
    private TraineeProfileResponse.TrainerSummary toInnerTrainerSummary(Trainer tr) {
        TraineeProfileResponse.TrainerSummary s = new TraineeProfileResponse.TrainerSummary();
        s.setUsername(tr.getUser().getUsername());
        s.setFirstName(tr.getUser().getFirstName());
        s.setLastName(tr.getUser().getLastName());
        s.setSpecialization(resolveSpecializationText(tr));
        return s;
    }

    private TrainerSummary toTopLevelTrainerSummary(Trainer tr) {
        TrainerSummary s = new TrainerSummary();
        s.setUsername(tr.getUser().getUsername());
        s.setFirstName(tr.getUser().getFirstName());
        s.setLastName(tr.getUser().getLastName());
        s.setSpecialization(resolveSpecializationText(tr));
        return s;
    }

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
