package com.be.repository;

import com.be.model.entity.Movie;
import com.be.model.entity.MovieTrailer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieTrailerRepository extends JpaRepository<MovieTrailer, Long> {
    List<MovieTrailer> findByMovieIdOrderByPublishedAtDesc(Long movieId);

    Page<MovieTrailer> findByMovieIdOrderByPublishedAtDesc(Long movieId, Pageable pageable);

    // You might also want these methods
    List<MovieTrailer> findByOrderByPublishedAtDesc(Pageable pageable); // Get latest trailers

    List<MovieTrailer> findByTypeAndOfficialTrueOrderByPublishedAtDesc(String type, Pageable pageable); // Get official trailers

    @Query(value = """
            SELECT mt.*
                FROM movie_trailers mt
                JOIN (
                    SELECT DISTINCT key, MAX(id) AS id
                    FROM movie_trailers
                    GROUP BY key
                ) sub ON mt.key = sub.key AND mt.id = sub.id
                ORDER BY mt.published_at DESC
            
            """, nativeQuery = true)
    Page<MovieTrailer> findDistinctOrderByPublishedAtDesc(Pageable pageable);

    // Optional: Get only official trailers
    @Query("SELECT mt FROM MovieTrailer mt " +
            "WHERE mt.official = true " +
            "ORDER BY mt.publishedAt DESC")
    Page<MovieTrailer> findLatestOfficialTrailers(Pageable pageable);

    Optional<MovieTrailer> findByTmdbId(String tmdbId);
    List<MovieTrailer> findByMovieOrderByPublishedAtDesc(Movie movie);
}
