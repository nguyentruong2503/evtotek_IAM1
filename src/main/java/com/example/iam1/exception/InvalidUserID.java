package com.example.iam1.exception;

public class InvalidUserID extends RuntimeException {
    public InvalidUserID(String message) {
        super(message);
    }
}
