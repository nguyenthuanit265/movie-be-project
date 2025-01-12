package com.be.repository;

import com.be.model.entity.Cast;
import com.be.model.entity.Movie;
import com.be.model.entity.MovieCast;
import com.be.model.entity.MovieCastId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieCastRepository extends JpaRepository<MovieCast, Long> {
    Page<MovieCast> findByMovie(Movie movie, Pageable pageable);

    Page<MovieCast> findByCast(Cast cast, Pageable pageable);

    @Query("SELECT mc FROM MovieCast mc WHERE mc.movie.id = :movieId ORDER BY mc.id.movieId")
    Page<MovieCast> findByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(mc) > 0 THEN true ELSE false END FROM MovieCast mc " +
            "WHERE mc.movie = :movie " +
            "AND mc.cast = :cast " +
            "AND mc.id.character = :character")
    boolean existsByMovieAndCastAndCharacter(
            @Param("movie") Movie movie,
            @Param("cast") Cast cast,
            @Param("character") String character
    );

//    boolean existsByMovieAndCastAndIdCharacter(Movie movie, Cast cast, String character);

    @Query("SELECT mc FROM MovieCast mc " +
            "WHERE mc.movie.id = :movieId " +
            "AND (LOWER(mc.id.character) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(mc.cast.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MovieCast> searchByMovie(
            @Param("movieId") Long movieId,
            @Param("search") String search,
            Pageable pageable
    );

    boolean existsById(MovieCastId id);
}
