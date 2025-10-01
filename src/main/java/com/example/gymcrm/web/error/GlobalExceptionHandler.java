package com.example.gymcrm.web.error;

import com.example.gymcrm.dto.ErrorResponse;
import com.example.gymcrm.exceptions.*;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private String tx() { return MDC.get("txId"); }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(tx(), "NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler({AlreadyActiveException.class, AlreadyDeactivatedException.class})
    public ResponseEntity<ErrorResponse> conflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(tx(), "CONFLICT", ex.getMessage()));
    }

    @ExceptionHandler(AuthFailedException.class)
    public ResponseEntity<ErrorResponse> unauthorized(AuthFailedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(tx(), "UNAUTHORIZED", ex.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> badRequest(Exception ex) {
        String msg = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(tx(), "BAD_REQUEST", msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> fallback(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(tx(), "INTERNAL_ERROR", "Unexpected error"));
    }
}
