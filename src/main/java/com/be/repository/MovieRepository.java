package com.be.repository;

import com.be.model.entity.CategoryType;
import com.be.model.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTmdbId(Long tmdbId);

    @Query("SELECT m FROM Movie m " +
            "WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(m.overview) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Movie> search(String query, Pageable pageable);

    @Query("SELECT DISTINCT m FROM Movie m " +
            "JOIN m.categories mc " +
            "WHERE mc.category = :category " +
            "AND mc.updatedAt >= :after " +
            "ORDER BY m.popularity DESC")
    Page<Movie> findMovieByCategory(
            @Param("category") CategoryType category,
            @Param("after") LocalDateTime after,
            Pageable pageable
    );
}
