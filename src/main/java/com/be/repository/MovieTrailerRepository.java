package com.be.repository;

import com.be.model.entity.MovieTrailer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieTrailerRepository extends JpaRepository<MovieTrailer, Long> {
    List<MovieTrailer> findByMovieIdOrderByPublishedAtDesc(Long movieId);
    Page<MovieTrailer> findByMovieIdOrderByPublishedAtDesc(Long movieId, Pageable pageable);

    // You might also want these methods
    List<MovieTrailer> findByOrderByPublishedAtDesc(Pageable pageable); // Get latest trailers
    List<MovieTrailer> findByTypeAndOfficialTrueOrderByPublishedAtDesc(String type, Pageable pageable); // Get official trailers
}
