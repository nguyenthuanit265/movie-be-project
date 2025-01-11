package com.be.service.impl;

import com.be.appexception.ResourceNotFoundException;
import com.be.model.dto.GenreDTO;
import com.be.model.dto.MovieDTO;
import com.be.model.dto.MovieTrailerDTO;
import com.be.model.entity.*;
import com.be.repository.*;
import com.be.service.MovieService;
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

    public MovieServiceImpl(MovieRepository movieRepository,
                            MovieTrailerRepository movieTrailerRepository,
                            UserRepository userRepository,
                            MovieRatingRepository ratingRepository,
                            ReviewRepository reviewRepository) {
        this.movieRepository = movieRepository;
        this.movieTrailerRepository = movieTrailerRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.reviewRepository = reviewRepository;
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

    public List<MovieDTO> toDTOList(List<Movie> movies) {
        return movies.stream()
                .map(this::toMovieDTO)
                .collect(Collectors.toList());
    }

    public Page<MovieDTO> toMovieDTOPage(Page<Movie> moviePage) {
        return moviePage.map(this::toMovieDTO);
    }

    public Page<Movie> searchMovies(String query, int page) {
        return movieRepository.search(
                query,
                PageRequest.of(page - 1, 20)
        );
    }


    @Transactional(readOnly = true)  // Add this
    public Page<MovieDTO> findAll(Pageable pageable) {
        return toMovieDTOPage(movieRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)  // Add this
    public Page<MovieDTO> findMovieByCategories(String category, Pageable pageable) {
        return toMovieDTOPage(movieRepository.findMovieByCategory(category, pageable));
    }

    public List<MovieTrailerDTO> getMovieTrailers(Long movieId) {
        List<MovieTrailer> trailers = movieTrailerRepository.findByMovieIdOrderByPublishedAtDesc(movieId);
        return trailers.stream()
                .map(MovieTrailerDTO::fromEntity)
                .collect(Collectors.toList());
    }

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

    // Get movie reviews
    public Page<Review> getMovieReviews(Long movieId, Pageable pageable) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));
        return reviewRepository.findByMovieOrderByCreatedAtDesc(movie, pageable);
    }
}
