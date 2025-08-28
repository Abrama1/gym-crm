package com.example.gymcrm.exceptions;

public class AlreadyActiveException extends RuntimeException {
    public AlreadyActiveException(String message) { super(message); }
}
