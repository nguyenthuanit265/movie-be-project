package com.be.service.external;

import com.be.model.entity.User;
import com.be.model.entity.UserRole;
import com.be.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@Service
@Slf4j
public class SystemUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public User getOrCreateSystemUser(String username, String authorName) {
        return transactionTemplate.execute(status -> {
            try {
                String systemEmail = "tmdb_" + username + "@system.local";

                // First try to find existing user
                User existingUser = userRepository.findByEmail(systemEmail).orElse(null);
                if (existingUser != null) {
                    return existingUser;
                }

                // Create new user if not found
                String hashPassword = BCrypt.hashpw("123456", BCrypt.gensalt());
                User newUser = User.builder()
                        .email(systemEmail)
                        .fullName(authorName)
                        .provider("TMDB")
                        .providerId(username)
                        .passwordHash(hashPassword)
                        .role(UserRole.system.name())
                        .isActive(true)
                        .build();

                User savedUser = userRepository.save(newUser);
                userRepository.flush();  // Ensure ID is generated

                return savedUser;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("Error creating system user for {}: {}", username, e.getMessage());
                throw e;
            }
        });
    }
}