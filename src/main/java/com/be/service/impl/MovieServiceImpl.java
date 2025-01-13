package com.be.service.impl;

import com.be.appexception.ResourceNotFoundException;
import com.be.model.dto.*;
import com.be.model.entity.*;
import com.be.repository.*;
import com.be.service.MovieService;
import com.be.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieTrailerRepository movieTrailerRepository;
    private final UserRepository userRepository;
    private final MovieRatingRepository ratingRepository;
    private final ReviewRepository reviewRepository;
    private final MovieCastRepository movieCastRepository;

    public MovieServiceImpl(MovieRepository movieRepository,
                            MovieTrailerRepository movieTrailerRepository,
                            UserRepository userRepository,
                            MovieRatingRepository ratingRepository,
                            ReviewRepository reviewRepository,
                            MovieCastRepository movieCastRepository) {
        this.movieRepository = movieRepository;
        this.movieTrailerRepository = movieTrailerRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.reviewRepository = reviewRepository;
        this.movieCastRepository = movieCastRepository;
    }

    public MovieDTO toMovieDTO(Movie movie) {
        if (movie == null) return null;

        return MovieDTO.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .runtime(movie.getRuntime())
                .posterPath(movie.getPosterPath())
                .backdropPath(movie.getBackdropPath())
                .popularity(movie.getPopularity())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .genres(movie.getGenres() != null ?
                        movie.getGenres().stream()
                                .map(genre -> GenreDTO.builder()
                                        .id(genre.getId())
                                        .name(genre.getName())
                                        .build())
                                .collect(Collectors.toSet()) :
                        null)
                .backdropUrl(movie.getBackdropUrl())
                .posterUrl(movie.getPosterUrl())
                .build();
    }

    public Page<MovieDTO> toMovieDTOPage(Page<Movie> moviePage, User user) {
        return moviePage.map(movie -> MovieDTO.fromEntity(movie, user));
    }

    @Transactional(readOnly = true)
    public Page<Movie> searchMovies(String query, int page) {
        return movieRepository.search(
                query,
                PageRequest.of(page - 1, 20)
        );
    }

    @Transactional(readOnly = true)  // Add this
    public Page<MovieDTO> findAll(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        User currentUser = userId != null ?
                userRepository.findById(userId).orElse(null) : null;
        return toMovieDTOPage(movieRepository.findAll(pageable), currentUser);
    }

    @Transactional(readOnly = true)  // Add this
    public Page<MovieDTO> findMovieByCategories(String category, Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        User currentUser = userId != null ?
                userRepository.findById(userId).orElse(null) : null;

        return toMovieDTOPage(movieRepository.findMovieByCategory(category, pageable), currentUser);
    }

    @Transactional(readOnly = true)
    public List<MovieTrailerDTO> getMovieTrailers(Long movieId) {
        List<MovieTrailer> trailers = movieTrailerRepository.findByMovieIdOrderByPublishedAtDesc(movieId);
        return trailers.stream()
                .map(MovieTrailerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MovieTrailerDTO> getMovieTrailers(Long movieId, Pageable pageable) {
        Page<MovieTrailer> trailers = movieTrailerRepository.findByMovieIdOrderByPublishedAtDesc(movieId, pageable);
        return trailers.map(MovieTrailerDTO::fromEntity);
    }

    // Rate a movie
    @Transactional(rollbackFor = Exception.class)
    public MovieRating rateMovie(Long movieId, Long userId, float rating) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "", "", ""));

        MovieRating movieRating = ratingRepository
                .findByMovieAndUser(movie, user)
                .orElse(MovieRating.builder()
                        .movie(movie)
                        .user(user)
                        .build());

        movieRating.setValue(rating);
        return ratingRepository.save(movieRating);
    }

    // Add/Remove from favorites
    @Transactional(rollbackFor = Exception.class)
    public void toggleFavorite(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "", "", ""));

        if (!CollectionUtils.isEmpty(user.getFavorites()) && user.getFavorites().contains(movie)) {
            user.getFavorites().remove(movie);
        } else {
            user.getFavorites().add(movie);
        }
        userRepository.save(user);
    }

    // Add/Remove from watchlist
    @Transactional(rollbackFor = Exception.class)
    public void toggleWatchlist(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "", "", ""));

        if (user.getWatchlist().contains(movie)) {
            user.getWatchlist().remove(movie);
        } else {
            user.getWatchlist().add(movie);
        }
        userRepository.save(user);
    }

    // Add review
    @Transactional(rollbackFor = Exception.class)
    public Review addReview(Long movieId, Long userId, String content) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "", "", ""));

        Review review = Review.builder()
                .movie(movie)
                .user(user)
                .content(content)
                .build();

        return reviewRepository.save(review);
    }

    // Watchlist
    @Transactional(rollbackFor = Exception.class)
    public void addToWatchlist(Long movieId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "", "", ""));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));

        user.getWatchlist().add(movie);
        userRepository.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeFromWatchlist(Long movieId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "", "", ""));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));

        user.getWatchlist().remove(movie);
        userRepository.save(user);
    }

    // Cast
    @Transactional(readOnly = true)
    public Page<CastDTO> getMovieCast(Long movieId, Pageable pageable) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));
        return movieCastRepository.findByMovie(movie, pageable)
                .map(CastDTO::fromEntity);
    }

    // Reviews
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getMovieReviews(Long movieId, Pageable pageable) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));
        return reviewRepository.findByMovieOrderByCreatedAtDesc(movie, pageable)
                .map(ReviewDTO::fromEntity);
    }

    // Recommendations based on user history
    @Transactional(readOnly = true)
    public Page<MovieDTO> getRecommendationsByUserHistory(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "", "", ""));

        // Get user's favorite genres based on watched movies
        List<Genre> favoriteGenres = movieRepository.findUserFavoriteGenres(userId);

        return movieRepository.findByGenresInOrderByPopularityDesc(favoriteGenres, pageable)
                .map(item -> MovieDTO.fromEntity(item, user));
    }

    // Recommendations based on current movie
    @Transactional(readOnly = true)
    public Page<MovieDTO> getSimilarMovies(Long movieId, Pageable pageable) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));

        // Get similar movies using genres and vector similarity
        return movieRepository.findSimilarMovies(movie.getId(), pageable)
                .map(item -> MovieDTO.fromEntity(item, null));
    }

    public Page<MovieTrailerDTO> getLatestTrailers(Pageable pageable) {
        return movieTrailerRepository
                .findAllByOrderByPublishedAtDesc(pageable)
                .map(MovieTrailerDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public MovieDetailDTO getMovieDetail(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));

        User currentUser = null;
        if (userId != null) {
            currentUser = userRepository.findById(userId)
                    .orElse(null);
        }

        return MovieDetailDTO.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .runtime(movie.getRuntime())
                .posterPath(movie.getPosterPath())
                .backdropPath(movie.getBackdropPath())
                .posterUrl(movie.getPosterUrl())
                .backdropUrl(movie.getBackdropUrl())
                .popularity(movie.getPopularity())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .genres(movie.getGenres().stream()
                        .map(GenreDTO::fromEntity)
                        .collect(Collectors.toSet()))
                .casts(movie.getCasts().stream()
                        .map(MovieCastDTO::fromEntity)
                        .collect(Collectors.toSet()))
                .trailers(movie.getMovieTrailers().stream()
                        .map(MovieTrailerDTO::fromEntity)
                        .collect(Collectors.toSet()))
                .isFavorite(currentUser != null && movie.getFavoritedBy().contains(currentUser))
                .isInWatchlist(currentUser != null && movie.getWatchlistedBy().contains(currentUser))
                .userRating(currentUser != null ? getUserRating(movie, currentUser) : null)
                .build();
    }

    private Float getUserRating(Movie movie, User user) {
        return movie.getRatings().stream()
                .filter(rating -> rating.getUser().equals(user))
                .map(MovieRating::getValue)
                .findFirst()
                .orElse(null);
    }
}
