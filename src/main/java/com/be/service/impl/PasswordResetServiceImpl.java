package com.be.service.impl;

import com.be.appexception.InvalidTokenException;
import com.be.appexception.PasswordMismatchException;
import com.be.appexception.TokenExpiredException;
import com.be.model.dto.auth.ResetPasswordRequest;
import com.be.model.entity.PasswordResetToken;
import com.be.model.entity.User;
import com.be.repository.PasswordResetTokenRepository;
import com.be.repository.UserRepository;
import com.be.service.EmailService;
import com.be.service.PasswordResetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetServiceImpl(UserRepository userRepository,
                                    PasswordResetTokenRepository tokenRepository,
                                    EmailService emailService,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createPasswordResetTokenForUser(User user) {
        String token = generateToken();

        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setUser(user);
        myToken.setToken(token);
        myToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        tokenRepository.save(myToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid password reset token"));

        if (token.isExpired()) {
            tokenRepository.delete(token);
            throw new TokenExpiredException("Password reset token has expired");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.delete(token);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
