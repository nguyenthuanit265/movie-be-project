package com.be.controller;

import com.be.model.base.AppResponse;
import com.be.service.external.TMDBService;
import com.be.model.dto.tmdb.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tmdb")
@Slf4j
public class TMDBMovieController {

    private final TMDBService tmdbService;
    private final HttpServletRequest request;

    public TMDBMovieController(TMDBService tmdbService, HttpServletRequest request) {
        this.tmdbService = tmdbService;
        this.request = request;
    }

    // Error handling
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("Error in TMDB controller: ", e);
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @GetMapping("/search")
    public ResponseEntity<TMDBSearchResultDTO> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(tmdbService.searchMulti(query, page));
    }

    // Search endpoints
    @GetMapping("/search/movies")
    public ResponseEntity<TMDBSearchResultDTO> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(tmdbService.searchMovies(query, page));
    }

    // Trending endpoints
    @GetMapping("/trending/movies/day")
    public ResponseEntity<TMDBTrendingResponse> getTrendingMoviesToday() {
        return ResponseEntity.ok(tmdbService.getTrending("day"));
    }

    @GetMapping("/trending/movies/week")
    public ResponseEntity<TMDBTrendingResponse> getTrendingMoviesThisWeek() {
        return ResponseEntity.ok(tmdbService.getTrending("week"));
    }

    // Popular movies
    @GetMapping("/movies/popular")
    public ResponseEntity<TMDBMovieResponse> getPopularMovies(
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(tmdbService.getPopularMovies(page));
    }

    // Movie details
    @GetMapping("/v1/movies/{movieId}")
    public ResponseEntity<TMDBMovieDTO> getMovieDetailsV1(
            @PathVariable Long movieId) {
        return ResponseEntity.ok(tmdbService.getMovieDetails(movieId));
    }

    // Get movie details
    @GetMapping("/v2/movies/{movieId}")
    public ResponseEntity<AppResponse<TMDBMovieDTO>> getMovieDetailsV2(
            @PathVariable Long movieId) {
        TMDBMovieDTO movie = tmdbService.getMovieDetails(movieId);
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movie details retrieved successfully",
                HttpStatus.OK.value(),
                movie
        ));
    }

    // Get movie credits
    @GetMapping("/{movieId}/credits")
    public ResponseEntity<AppResponse<TMDBCreditsResponse>> getMovieCredits(
            @PathVariable Long movieId) {
        TMDBCreditsResponse credits = tmdbService.getMovieCredits(movieId);
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movie credits retrieved successfully",
                HttpStatus.OK.value(),
                credits
        ));
    }

    // Get movie reviews
    @GetMapping("/{movieId}/reviews")
    public ResponseEntity<AppResponse<TMDBReviewResponse>> getMovieReviews(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "1") int page) {
        TMDBReviewResponse reviews = tmdbService.getMovieReviews(movieId, page);
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movie reviews retrieved successfully",
                HttpStatus.OK.value(),
                reviews
        ));
    }

    // Rate movie
    @PostMapping("/{movieId}/rating")
    public ResponseEntity<AppResponse<Void>> rateMovie(
            @PathVariable Long movieId,
            @RequestBody double rating) {
        tmdbService.rateMovie(movieId, rating);
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movie rated successfully",
                HttpStatus.OK.value(),
                null
        ));
    }

    // Add to favorites
    @PostMapping("/{movieId}/favorite")
    public ResponseEntity<AppResponse<Void>> addToFavorites(
            @PathVariable Long movieId,
            @RequestParam String accountId,
            @RequestParam boolean favorite) {
        tmdbService.addToFavorites(movieId, accountId, favorite);
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                favorite ? "Added to favorites" : "Removed from favorites",
                HttpStatus.OK.value(),
                null
        ));
    }

    // Add to watchlist
    @PostMapping("/{movieId}/watchlist")
    public ResponseEntity<AppResponse<Void>> addToWatchlist(
            @PathVariable Long movieId,
            @RequestParam String accountId,
            @RequestParam boolean watchlist) {
        tmdbService.addToWatchlist(movieId, accountId, watchlist);
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                watchlist ? "Added to watchlist" : "Removed from watchlist",
                HttpStatus.OK.value(),
                null
        ));
    }
//
//    // Movie videos/trailers
//    @GetMapping("/movies/{movieId}/videos")
//    public ResponseEntity<VideoResponse> getMovieVideos(
//            @PathVariable Long movieId) {
//        return ResponseEntity.ok(tmdbService.getMovieVideos(movieId));
//    }
//
//    // Movie credits
//    @GetMapping("/movies/{movieId}/credits")
//    public ResponseEntity<TMDBCreditsDTO> getMovieCredits(
//            @PathVariable Long movieId) {
//        return ResponseEntity.ok(tmdbService.getMovieCredits(movieId));
//    }

//    // Genres
//    @GetMapping("/genres")
//    public ResponseEntity<GenreResponse> getGenres() {
//        return ResponseEntity.ok(tmdbService.getGenres());
//    }
}
