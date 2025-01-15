package com.be.service.impl;

import com.be.appexception.ResourceNotFoundException;
import com.be.model.dto.MovieDTO;
import com.be.model.entity.Genre;
import com.be.model.entity.Movie;
import com.be.model.entity.User;
import com.be.repository.MovieRepository;
import com.be.repository.UserRepository;
import com.be.service.MovieRecommendationService;
import com.be.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MovieRecommendationServiceImpl implements MovieRecommendationService {

    private final MovieRepository movieRepository;

    private final UserRepository userRepository;

    public MovieRecommendationServiceImpl(MovieRepository movieRepository,
                                          UserRepository userRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieDTO> getRecommendationsByUserHistory(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get user's favorite genres based on watchlist and favorites
        Set<Genre> favoriteGenres = new HashSet<>();

        // Add genres from watchlist
        user.getWatchlist().stream()
                .flatMap(movie -> movie.getGenres().stream())
                .forEach(favoriteGenres::add);

        // Add genres from favorites
        user.getFavorites().stream()
                .flatMap(movie -> movie.getGenres().stream())
                .forEach(favoriteGenres::add);

        // Get recommendations based on genres
        return movieRepository.findSimilarMoviesByGenres(
                favoriteGenres.stream().map(Genre::getId).collect(Collectors.toList()),
                user.getWatchlist().stream().map(Movie::getId).collect(Collectors.toList()),
                user.getFavorites().stream().map(Movie::getId).collect(Collectors.toList()),
                pageable
        ).map(item -> MovieDTO.fromEntity(item, user));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieDTO> getRecommendationsByVectorSimilarity(Long movieId, Pageable pageable) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser;
        if (currentUserId != null) {
            currentUser = userRepository.findById(currentUserId)
                    .orElse(null);
        } else {
            currentUser = null;
        }

        Page<MovieDTO> res = movieRepository.findSimilarMoviesByVector(
                movie.getId(),
                movie.getGenres().stream().map(Genre::getId).collect(Collectors.toList()),
                pageable
        ).map(item -> MovieDTO.fromEntity(item, currentUser));
        return res;
    }
}
