package com.be.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
}
