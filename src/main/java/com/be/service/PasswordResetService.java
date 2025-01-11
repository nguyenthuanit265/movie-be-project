package com.be.service;

import com.be.model.dto.auth.ResetPasswordRequest;
import com.be.model.entity.User;

public interface PasswordResetService {
    void createPasswordResetTokenForUser(User user);

    void resetPassword(ResetPasswordRequest request);
}
