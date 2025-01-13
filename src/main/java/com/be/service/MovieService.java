package com.be.service;

import com.be.model.dto.*;
import com.be.model.entity.Movie;
import com.be.model.entity.MovieRating;
import com.be.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface MovieService {
    Page<Movie> searchMovies(String query, int page);

    Page<MovieDTO> findAll(Pageable pageable);

    Page<MovieDTO> findMovieByCategories(String category, Pageable pageable);

    List<MovieTrailerDTO> getMovieTrailers(Long movieId);

    Page<MovieTrailerDTO> getMovieTrailers(Long movieId, Pageable pageable);

    MovieRating rateMovie(Long movieId, Long userId, float rating);

    void toggleFavorite(Long movieId, Long userId);

    void toggleWatchlist(Long movieId, Long userId);

    Review addReview(Long movieId, Long userId, String content);

    Page<ReviewDTO> getMovieReviews(Long movieId, Pageable pageable);

    Page<MovieDTO> getRecommendationsByUserHistory(Long userId, Pageable pageable);

    Page<MovieDTO> getSimilarMovies(Long movieId, Pageable pageable);

    Page<CastDTO> getMovieCast(Long movieId, Pageable pageable);

    void removeFromWatchlist(Long movieId, Long userId);

    void addToWatchlist(Long movieId, Long userId);

    Page<MovieTrailerDTO> getLatestTrailers(Pageable pageable);

    MovieDetailDTO getMovieDetail(Long movieId, Long userId);
}
