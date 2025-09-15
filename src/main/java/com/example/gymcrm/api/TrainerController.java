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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Api(tags = "Trainers")
@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainingTypeDao trainingTypeDao; // used only to validate training type at registration

    public TrainerController(TrainerService trainerService, TrainingTypeDao trainingTypeDao) {
        this.trainerService = trainerService;
        this.trainingTypeDao = trainingTypeDao;
    }

    private static Credentials creds(HttpServletRequest req) {
        return new Credentials(req.getHeader("X-Username"), req.getHeader("X-Password"));
    }

    @ApiOperation("Register a new trainer")
    @PostMapping("/register")
    public RegistrationResponse register(@RequestBody @Valid TrainerRegistrationRequest body) {
        // validate training type name exists (constant table)
        trainingTypeDao.findByName(body.getSpecialization())
                .orElseThrow(() -> new NotFoundException("Training type not found: " + body.getSpecialization()));

        Trainer trainer = new Trainer();
        trainer.setSpecialization(body.getSpecialization());

        Trainer saved = trainerService.create(trainer, body.getFirstName(), body.getLastName(), true);
        return new RegistrationResponse(saved.getUser().getUsername(), saved.getUser().getPassword());
    }

    @ApiOperation("Get trainer profile by username")
    @GetMapping("/{username}")
    public TrainerProfileResponse getProfile(@PathVariable String username, HttpServletRequest req) {
        Trainer tr = trainerService.getByUsername(creds(req), username);
        return toProfile(tr);
    }

    @ApiOperation("Update trainer profile (first/last name, active); specialization = read-only")
    @PutMapping("/{username}")
    public TrainerProfileResponse update(@PathVariable String username,
                                         @RequestBody @Valid UpdateTrainerRequest body,
                                         HttpServletRequest req) {
        // authorize + load my entity
        Trainer me = trainerService.getByUsername(creds(req), username);

        // patch allowed fields on nested User
        User u = me.getUser();
        u.setFirstName(body.getFirstName());
        u.setLastName(body.getLastName());
        u.setActive(Boolean.TRUE.equals(body.getActive()));
        me.setUser(u);

        // DO NOT touch specialization here (read-only)
        Trainer saved = trainerService.update(me);
        return toProfile(saved);
    }

    @ApiOperation("Activate / De-Activate trainer (not idempotent)")
    @PatchMapping("/{username}/activation")
    public ResponseEntity<Void> activation(@PathVariable String username,
                                           @RequestBody @Valid ActivationRequest body,
                                           HttpServletRequest req) {
        if (Boolean.TRUE.equals(body.getActive())) {
            trainerService.activate(creds(req));
        } else {
            trainerService.deactivate(creds(req));
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
