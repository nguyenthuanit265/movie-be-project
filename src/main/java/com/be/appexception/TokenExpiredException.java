package com.be.appexception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}