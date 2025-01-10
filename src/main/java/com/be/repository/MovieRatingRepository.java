package com.be.repository;

import com.be.model.entity.Movie;
import com.be.model.entity.MovieRating;
import com.be.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRatingRepository extends JpaRepository<MovieRating, Long> {
    Optional<MovieRating> findByMovieAndUser(Movie movie, User user);
    Page<MovieRating> findByMovie(Movie movie, Pageable pageable);
}
