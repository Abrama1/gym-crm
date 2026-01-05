package com.example.gymcrm.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String tx = request.getHeader("X-Transaction-Id");
        if (tx == null || tx.isBlank()) {
            tx = UUID.randomUUID().toString();
        }

        MDC.put("txId", tx);
        response.setHeader("X-Transaction-Id", tx);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("txId");
        }
    }
}
