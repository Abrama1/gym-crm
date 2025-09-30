package com.example.gymcrm.web.auth;

import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.exceptions.AuthFailedException;
import com.example.gymcrm.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    public static final String SESSION_USER = "AUTH_USER";
    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(SESSION_USER) != null) {
            return true;
        }

        String u = req.getHeader("X-Username");
        String p = req.getHeader("X-Password");
        if (u != null && p != null) {
            Credentials c = new Credentials();
            c.setUsername(u);
            c.setPassword(p);

            boolean ok = false;
            try {
                authService.authenticateTrainee(c);
                ok = true;
            } catch (AuthFailedException ignore) {
                try {
                    authService.authenticateTrainer(c);
                    ok = true;
                } catch (AuthFailedException ignore2) { /* still false */ }
            }

            if (ok) {
                HttpSession s = req.getSession(true);
                s.setAttribute(SESSION_USER, u);
                MDC.put("user", u);
                return true;
            }
        }

        return true;
    }
}
