package com.example.gymcrm.exceptions;

public class AlreadyDeactivatedException extends RuntimeException {
    public AlreadyDeactivatedException(String message) { super(message); }
}
