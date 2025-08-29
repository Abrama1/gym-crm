package com.example.gymcrm.exceptions;

public class AuthFailedException extends RuntimeException {
    public AuthFailedException(String message) { super(message); }
}
