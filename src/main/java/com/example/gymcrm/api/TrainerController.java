package com.example.gymcrm.api;

import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.dto.*;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Api(tags = "Trainers")
@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainingTypeDao trainingTypeDao;

    public TrainerController(TrainerService trainerService, TrainingTypeDao trainingTypeDao) {
        this.trainerService = trainerService;
        this.trainingTypeDao = trainingTypeDao;
    }

    private static String me() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return (a == null) ? null : a.getName();
    }
    private static Credentials selfCreds() {
        Credentials c = new Credentials();
        c.setUsername(me());
        return c;
    }

    @ApiOperation("Register a new trainer")
    @PostMapping("/register")
    public RegistrationResponse register(@RequestBody @Valid TrainerRegistrationRequest body) {
        trainingTypeDao.findByName(body.getSpecialization())
                .orElseThrow(() -> new NotFoundException("Training type not found: " + body.getSpecialization()));

        Trainer trainer = new Trainer();
        trainer.setSpecialization(body.getSpecialization());

        Trainer saved = trainerService.create(trainer, body.getFirstName(), body.getLastName(), true);
        return new RegistrationResponse(saved.getUser().getUsername(), saved.getUser().getPassword());
    }

    @ApiOperation("Get trainer profile by username")
    @GetMapping("/{username}")
    public TrainerProfileResponse getProfile(@PathVariable String username) {
        Trainer tr = trainerService.getByUsername(selfCreds(), username);
        return toProfile(tr);
    }

    @ApiOperation("Update trainer profile (first/last name, active); specialization = read-only")
    @PutMapping("/{username}")
    public TrainerProfileResponse update(@PathVariable String username,
                                         @RequestBody @Valid UpdateTrainerRequest body) {

        Trainer meEntity = trainerService.getByUsername(selfCreds(), username);

        User u = meEntity.getUser();
        u.setFirstName(body.getFirstName());
        u.setLastName(body.getLastName());
        u.setActive(Boolean.TRUE.equals(body.getActive()));
        meEntity.setUser(u);

        Trainer saved = trainerService.update(meEntity);
        return toProfile(saved);
    }

    @ApiOperation("Activate / De-Activate trainer (not idempotent)")
    @PatchMapping("/{username}/activation")
    public ResponseEntity<Void> activation(@PathVariable String username,
                                           @RequestBody @Valid ActivationRequest body) {
        // use current principal (self)
        if (Boolean.TRUE.equals(body.getActive())) {
            trainerService.activate(selfCreds());
        } else {
            trainerService.deactivate(selfCreds());
        }
        return ResponseEntity.ok().build();
    }

    // ---- mapping helpers ----
    private static TrainerProfileResponse toProfile(Trainer tr) {
        TrainerProfileResponse res = new TrainerProfileResponse();
        res.setUsername(tr.getUser().getUsername());
        res.setFirstName(tr.getUser().getFirstName());
        res.setLastName(tr.getUser().getLastName());
        res.setSpecialization(tr.getSpecialization());
        res.setActive(tr.getUser().isActive());
        res.setTrainees(tr.getTrainees().stream()
                .map(TrainerController::toSummary)
                .collect(Collectors.toList()));
        return res;
    }

    static TrainerProfileResponse.TraineeSummary toSummary(Trainee t) {
        return new TrainerProfileResponse.TraineeSummary(
                t.getUser().getUsername(),
                t.getUser().getFirstName(),
                t.getUser().getLastName()
        );
    }
}
