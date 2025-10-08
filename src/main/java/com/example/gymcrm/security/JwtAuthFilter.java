package com.example.gymcrm.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final TokenBlacklist blacklist;

    public JwtAuthFilter(JwtService jwt, TokenBlacklist blacklist) {
        this.jwt = jwt;
        this.blacklist = blacklist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            String token = h.substring(7).trim();

            if (!blacklist.isRevoked(token)) {
                try {
                    Claims c = jwt.parse(token);
                    String user = c.getSubject();
                    String role = String.valueOf(c.get("role"));

                    var auth = new AbstractAuthenticationToken(
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))) {
                        @Override public Object getCredentials() { return token; }
                        @Override public Object getPrincipal() { return user; }
                    };
                    auth.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception ignore) {
                    // invalid/expired token -> no auth; protected endpoints will 401 via entry point
                    SecurityContextHolder.clearContext();
                }
            }
        }

        chain.doFilter(req, res);
    }
}
