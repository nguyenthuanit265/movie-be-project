package com.be.repository;

import com.be.model.entity.Movie;
import com.be.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByMovieOrderByCreatedAtDesc(Movie movie, Pageable pageable);
}
