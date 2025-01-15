package com.be.service;

import com.be.model.entity.User;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
    public void sendVerificationEmail(User user, String token);
}
