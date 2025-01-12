package com.be.appexception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}