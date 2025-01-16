package com.be.service.impl;

import com.be.appexception.BadRequestException;
import com.be.appexception.InvalidTokenException;
import com.be.appexception.ResourceNotFoundException;
import com.be.appexception.TokenExpiredException;
import com.be.config.JwtTokenProvider;
import com.be.model.entity.User;
import com.be.model.entity.VerificationToken;
import com.be.repository.UserRepository;
import com.be.repository.VerificationTokenRepository;
import com.be.service.AuthService;
import com.be.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
//    private final FirebaseAuth firebaseAuth;
    private final JdbcTemplate jdbcTemplate;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           VerificationTokenRepository tokenRepository,
                           EmailService emailService,
                           JdbcTemplate jdbcTemplate,
                           JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.jdbcTemplate = jdbcTemplate;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(user, token);
        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(user, token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String verifyAccount(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        if (verificationToken.isExpired()) {
            tokenRepository.delete(verificationToken);
            throw new TokenExpiredException("Token has expired");
        }

        User user = verificationToken.getUser();
        if (user.getIsActive()) {
            return "Account is already verified";
        }

        user.setIsActive(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);

        return "Account verified successfully";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resendVerificationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getIsActive()) {
            throw new BadRequestException("Account is already verified");
        }

        // Delete old token if exists
        tokenRepository.findByUser(user)
                .ifPresent(tokenRepository::delete);

        // Create new token
        createVerificationToken(user);
    }

//    public AuthResponse loginWithGoogle(String idToken) {
//        try {
//            // Verify the Firebase ID token
//            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
//
//            // Get user info from token
//            String email = decodedToken.getEmail();
//            String name = decodedToken.getName();
//            String picture = decodedToken.getPicture();
//            String firebaseUid = decodedToken.getUid();
//
//            // Check if user exists
//            String sql = "SELECT id, role FROM users WHERE email = ?";
//            var userOptional = jdbcTemplate.query(sql,
//                    (rs, rowNum) -> new Object[]{
//                            rs.getLong("id"),
//                            rs.getString("role")
//                    },
//                    email
//            ).stream().findFirst();
//
//            Long userId;
//            String role;
//
//            if (userOptional.isEmpty()) {
//                // Create new user
//                String insertSql = """
//                            INSERT INTO users (email, full_name, image_url, provider, provider_id, is_active, role)
//                            VALUES (?, ?, ?, 'google', ?, true, 'user')
//                            RETURNING id
//                        """;
//
//                userId = jdbcTemplate.queryForObject(insertSql, Long.class,
//                        email, name, picture, firebaseUid
//                );
//                role = "user";
//            } else {
//                Object[] userData = userOptional.get();
//                userId = (Long) userData[0];
//                role = (String) userData[1];
//            }
//
//            // Generate JWT token
//            String token = tokenProvider.generateToken(userId, email, role);
//
//            return AuthResponse.builder()
//                    .email(email)
//                    .name(name)
//                    .accessToken(token)
//                    .build();
//
//        } catch (Exception e) {
////            throw new RuntimeException("Authentication failed", e);
//            return AuthResponse.builder().build();
//        }
//    }
}
