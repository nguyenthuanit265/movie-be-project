package com.be.controller;

import com.be.service.external.TMDBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tmdb/sync")
@Slf4j
public class TMDBSyncController {
    private final TMDBService tmdbService;

    @Autowired
    public TMDBSyncController(TMDBService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @PostMapping("/trending")
    public ResponseEntity<?> syncTrendingMovies() {
        tmdbService.syncTrendingMovies();
        return ResponseEntity.ok("Started syncing trending movies");
    }

//    @PostMapping("/popular")
//    public ResponseEntity<?> syncPopularMovies() {
//        tmdbService.syncPopularMovies();
//        return ResponseEntity.ok("Started syncing popular movies");
//    }
}
