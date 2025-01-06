package com.be.controller;

import com.be.external.TMDBService;
import com.be.model.dto.tmdb.MovieTrailerDTO;
import com.be.model.dto.tmdb.SearchResultDTO;
import com.be.model.dto.tmdb.TMDBMovieDTO;
import com.be.model.dto.tmdb.TrendingItemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tmdb/movies")
public class TMDBMovieController {

    private final TMDBService tmdbService;

    public TMDBMovieController(TMDBService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResultDTO> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(tmdbService.searchMulti(query, page));
    }

    @GetMapping("/trending/today")
    public ResponseEntity<List<TrendingItemDTO>> getTrendingToday() {
        return ResponseEntity.ok(tmdbService.getTrendingMoviesToday());
    }

    @GetMapping("/trending/week")
    public ResponseEntity<List<TrendingItemDTO>> getTrendingThisWeek() {
        return ResponseEntity.ok(tmdbService.getTrendingMoviesThisWeek());
    }

    @GetMapping("/trailers/latest")
    public ResponseEntity<List<MovieTrailerDTO>> getLatestTrailers() {
        return ResponseEntity.ok(tmdbService.getLatestTrailers());
    }

    @GetMapping("/popular")
    public ResponseEntity<List<TMDBMovieDTO>> getPopularMovies(
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(tmdbService.getPopularMovies(page));
    }
}
