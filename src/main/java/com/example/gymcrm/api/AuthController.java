package com.example.gymcrm.api;

import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.security.BruteForceService;
import com.example.gymcrm.security.JwtService;
import com.example.gymcrm.security.TokenBlacklist;
import com.example.gymcrm.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;
    private final JwtService jwt;
    private final BruteForceService brute;
    private final TokenBlacklist blacklist;

    public AuthController(AuthService auth, JwtService jwt,
                          BruteForceService brute, TokenBlacklist blacklist) {
        this.auth = auth;
        this.jwt = jwt;
        this.brute = brute;
        this.blacklist = blacklist;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid Credentials body) {
        String u = body.getUsername();
        if (brute.isLocked(u)) {
            return ResponseEntity.status(423) // Locked
                    .body(Map.of("error", "Account locked", "retryAfterSec", brute.getLockSeconds()));
        }

        try {
            // Try trainee, then trainer
            String role;
            try {
                Trainee t = auth.authenticateTrainee(body);
                role = "trainee";
            } catch (AuthFailedException ex) {
                Trainer tr = auth.authenticateTrainer(body); // throws if bad
                role = "trainer";
            }

            brute.reset(u);
            String token = jwt.generate(u, role);
            return ResponseEntity.ok(Map.of(
                    "tokenType", "Bearer",
                    "token", token
            ));
        } catch (AuthFailedException ex) {
            brute.recordFailure(u);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req) {
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            blacklist.revoke(h.substring(7));
        }
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
