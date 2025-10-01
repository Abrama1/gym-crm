package com.example.gymcrm.web.auth;

import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {
    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        // Paths are excluded in WebConfig; if we are here, auth is required.
        String u = req.getHeader("X-Username");
        String p = req.getHeader("X-Password");
        if (u == null || p == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setHeader("WWW-Authenticate", "GymCRM realm=\"api\"");
            return false;
        }

        Credentials c = new Credentials();
        c.setUsername(u);
        c.setPassword(p);

        try {
            // Try trainee, then trainer
            try {
                authService.authenticateTrainee(c);
            } catch (AuthFailedException ignore) {
                authService.authenticateTrainer(c);
            }
            MDC.put("user", u);
            return true;
        } catch (AuthFailedException e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setHeader("WWW-Authenticate", "GymCRM realm=\"api\"");
            return false;
        }
    }
}
