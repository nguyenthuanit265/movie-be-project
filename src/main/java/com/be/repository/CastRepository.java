package com.be.repository;

import com.be.model.entity.Cast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CastRepository extends JpaRepository<Cast, Long> {
    Optional<Cast> findByTmdbId(Long tmdbId);
}
