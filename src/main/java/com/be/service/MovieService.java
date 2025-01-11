package com.be.service;

import com.be.model.dto.MovieDTO;
import com.be.model.dto.MovieTrailerDTO;
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

    Page<Review> getMovieReviews(Long movieId, Pageable pageable);
}
