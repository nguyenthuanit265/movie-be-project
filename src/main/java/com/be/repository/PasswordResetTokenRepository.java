package com.be.repository;

import com.be.model.entity.PasswordResetToken;
import com.be.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    // You might also want these additional methods
    Optional<PasswordResetToken> findByUserAndToken(User user, String token);
    void deleteByExpiryDateLessThan(LocalDateTime now);  // Clean up expired tokens
    boolean existsByToken(String token);
}
