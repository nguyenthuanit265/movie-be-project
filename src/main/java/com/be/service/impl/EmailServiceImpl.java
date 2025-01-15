package com.be.service.impl;

import com.be.model.entity.User;
import com.be.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.username:''}")
    private String fromEmail;

    @Value("${app.fe.host:'http://localhost:5173'}")
    private String hostFrontEnd;

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Password Reset Request");
            message.setText("To reset your password, click the link below:\n\n" +
                    hostFrontEnd + "/reset-password?token=" + token);

            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending password reset email: ", e);
            throw new RuntimeException("Error sending email");
        }
    }


    @Override
    public void sendVerificationEmail(User user, String token) {
        try {
            String verificationUrl = hostFrontEnd + "/verify?token=" + token;

            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(fromEmail);
            email.setTo(user.getEmail());
            email.setSubject("Account Verification");
            email.setText("Please click the link below to verify your email:\n\n" + verificationUrl);

            mailSender.send(email);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending verification email: ", e);
            throw new RuntimeException("Failed to send verification email");
        }
    }
}
