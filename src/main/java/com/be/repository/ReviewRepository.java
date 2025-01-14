package com.be.repository;

import com.be.model.entity.Movie;
import com.be.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByMovieOrderByCreatedAtDesc(Movie movie, Pageable pageable);

    Optional<Review> findByTmdbId(String tmdbId);

    List<Review> findByMovieOrderByCreatedAtDesc(Movie movie);

    boolean existsByTmdbId(String tmdbId);
}
