package com.be.controller;

import com.be.service.external.TMDBService;
import com.be.model.dto.tmdb.*;
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

    public TMDBMovieController(TMDBService tmdbService) {
        this.tmdbService = tmdbService;
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
    @GetMapping("/movies/{movieId}")
    public ResponseEntity<TMDBMovieDTO> getMovieDetails(
            @PathVariable Long movieId) {
        return ResponseEntity.ok(tmdbService.getMovieDetails(movieId));
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

    // Error handling
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("Error in TMDB controller: ", e);
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
