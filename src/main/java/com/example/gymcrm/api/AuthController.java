package com.example.gymcrm.api;

import com.example.gymcrm.dto.ChangePasswordRequest;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.service.AuthService;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Auth")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TrainerService trainerService;
    private final TraineeService traineeService;

    public AuthController(AuthService authService,
                          TrainerService trainerService,
                          TraineeService traineeService) {
        this.authService = authService;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
    }

    @ApiOperation("Login (200 OK if credentials are valid)")
    @GetMapping("/login")
    public ResponseEntity<Void> login(@RequestParam String username,
                                      @RequestParam String password) {
        // try authenticate as trainer, if fails, try trainee
        try {
            authService.authenticateTrainer(new Credentials(username, password));
        } catch (AuthFailedException e) {
            authService.authenticateTrainee(new Credentials(username, password));
        }
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Change password (works for either trainer or trainee)")
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest body) {
        var creds = new Credentials(body.getUsername(), body.getOldPassword());
        try {
            trainerService.changePassword(creds, body.getNewPassword());
        } catch (AuthFailedException e) {
            traineeService.changePassword(creds, body.getNewPassword());
        }
        return ResponseEntity.ok().build();
    }
}
