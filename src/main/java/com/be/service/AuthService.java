package com.be.service;

import com.be.model.entity.User;

public interface AuthService {
    void createVerificationToken(User user);
    String verifyAccount(String token);
    void resendVerificationToken(String email);
}
