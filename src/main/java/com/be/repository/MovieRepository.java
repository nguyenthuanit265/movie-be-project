package com.be.repository;

import com.be.model.entity.CategoryType;
import com.be.model.entity.Genre;
import com.be.model.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
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
            "ORDER BY m.popularity DESC")
    Page<Movie> findMovieByCategory(
            @Param("category") String category,
            Pageable pageable
    );

    // Find user's favorite genres based on watched/rated movies
    @Query("""
                SELECT DISTINCT g FROM Genre g
                JOIN g.movies m
                JOIN m.ratings r
                WHERE r.user.id = :userId
                GROUP BY g.id
                ORDER BY COUNT(g.id) DESC
                LIMIT 5
            """)
    List<Genre> findUserFavoriteGenres(@Param("userId") Long userId);

    // Find movies by genres
    @Query("""
                SELECT DISTINCT m FROM Movie m
                JOIN m.genres g
                WHERE g IN :genres
                ORDER BY m.popularity DESC
            """)
    Page<Movie> findByGenresInOrderByPopularityDesc(
            @Param("genres") Collection<Genre> genres,
            Pageable pageable
    );

    // Find similar movies
    @Query("""
                SELECT DISTINCT m FROM Movie m
                JOIN m.genres g
                WHERE g IN (
                    SELECT mg FROM Movie mo
                    JOIN mo.genres mg
                    WHERE mo.id = :movieId
                )
                AND m.id != :movieId
                ORDER BY m.voteAverage DESC, m.popularity DESC
            """)
    Page<Movie> findSimilarMovies(
            @Param("movieId") Long movieId,
            Pageable pageable
    );

    // Alternative vector-based similarity if using pgvector
    @Query(value = """
                SELECT m.* FROM movies m
                JOIN movie_vectors mv1 ON m.id = mv1.movie_id
                JOIN movie_vectors mv2 ON mv2.movie_id = :movieId
                WHERE m.id != :movieId
                ORDER BY mv1.embedding <-> mv2.embedding
            """, nativeQuery = true)
    Page<Movie> findSimilarMoviesByVector(
            @Param("movieId") Long movieId,
            Pageable pageable
    );
}
