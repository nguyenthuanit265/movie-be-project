package com.be.repository;

import com.be.model.entity.User;
import com.be.model.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(User user);

    void deleteByExpiryDateLessThan(LocalDateTime now);

    boolean existsByToken(String token);

    @Query("SELECT vt FROM VerificationToken vt " +
            "WHERE vt.user.email = :email AND vt.expiryDate > :now")
    Optional<VerificationToken> findValidTokenByEmail(
            @Param("email") String email,
            @Param("now") LocalDateTime now
    );
}
