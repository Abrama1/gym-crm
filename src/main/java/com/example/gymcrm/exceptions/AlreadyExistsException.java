package com.example.gymcrm.exceptions;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) { super(message); }
}
