package com.example.gymcrm.api;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.dto.LoginRequest;
import com.example.gymcrm.dto.LoginResponse;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.security.BruteForceService;
import com.example.gymcrm.security.JwtService;
import com.example.gymcrm.security.TokenBlacklist;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDao userDao;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final BruteForceService brute;
    private final TokenBlacklist blacklist;

    public AuthController(UserDao userDao,
                          TraineeDao traineeDao,
                          TrainerDao trainerDao,
                          PasswordEncoder encoder,
                          JwtService jwt,
                          BruteForceService brute,
                          TokenBlacklist blacklist) {
        this.userDao = userDao;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.encoder = encoder;
        this.jwt = jwt;
        this.brute = brute;
        this.blacklist = blacklist;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest body) {
        String username = body.getUsername();

        if (brute.isLocked(username)) {
            throw new ResponseStatusException(HttpStatus.LOCKED,
                    "Account locked. Try again in " + brute.getLockSeconds() + "s");
        }

        User u = userDao.findByUsername(username)
                .orElseThrow(() -> {
                    brute.recordFailure(username);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
                });

        if (!u.isActive()) {
            brute.recordFailure(username);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deactivated");
        }

        if (!encoder.matches(body.getPassword(), u.getPassword())) {
            brute.recordFailure(username);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String role = resolveRole(u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Profile not found"));

        brute.reset(username);

        String token = jwt.generate(username, role);
        return new LoginResponse(token, "Bearer", username, role);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklist.revoke(token);
        }
        return ResponseEntity.noContent().build();
    }

    private Optional<String> resolveRole(Long userId) {
        if (traineeDao.findByUserId(userId).isPresent()) return Optional.of("trainee");
        if (trainerDao.findByUserId(userId).isPresent()) return Optional.of("trainer");
        return Optional.empty();
    }
}
