package com.be.controller;

import com.be.model.base.AppResponse;
import com.be.model.entity.Movie;
import com.be.repository.MovieRepository;
import com.be.service.external.TMDBService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tmdb/sync")
@Slf4j
public class TMDBSyncController {
    private final TMDBService tmdbService;
    private final HttpServletRequest request;
    private final MovieRepository movieRepository;

    @Autowired
    public TMDBSyncController(TMDBService tmdbService,
                              HttpServletRequest request,
                              MovieRepository movieRepository) {
        this.tmdbService = tmdbService;
        this.request = request;
        this.movieRepository = movieRepository;
    }

    @PostMapping("/trending")
    public ResponseEntity<AppResponse<String>> syncTrendingMovies() {
        tmdbService.syncTrendingMovies(); // This will run in background

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Trending movies sync started in background",
                HttpStatus.OK.value(),
                "Sync process started"
        ));
    }

    @PostMapping("/popular")
    public ResponseEntity<AppResponse<String>> syncPopularMovies() {
        tmdbService.syncPopularMovies(); // This will run in background

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Popular movies sync started in background",
                HttpStatus.OK.value(),
                "Sync process started"
        ));
    }

    @PostMapping("/trailers")
    public ResponseEntity<AppResponse<String>> syncLatestTrailers() {
        tmdbService.syncLatestTrailers();

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Latest trailers sync started in background",
                HttpStatus.OK.value(),
                "Sync process started"
        ));
    }

    @PostMapping("/movies/cast")
    public ResponseEntity<AppResponse<String>> syncAllMovieCasts() {
        tmdbService.syncAllMovieCasts();

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Batch movie cast sync started",
                HttpStatus.OK.value(),
                "Sync process started"
        ));
    }

    // Optional: Sync specific movies
    @PostMapping("/movies/cast/batch")
    public ResponseEntity<AppResponse<String>> syncMovieCastsBatch(
            @RequestBody List<Long> movieIds) {
        List<Movie> movies = movieRepository.findAllById(movieIds);
        for (Movie movie : movies) {
            tmdbService.syncMovieCast(movie);
        }

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Batch movie cast sync started",
                HttpStatus.OK.value(),
                String.format("Started syncing casts for %d movies", movieIds.size())
        ));
    }
}
