package com.be.controller;


import com.be.appexception.ResourceNotFoundException;
import com.be.service.CastService;
import com.be.service.MovieRecommendationService;
import com.be.utils.SecurityUtils;
import com.be.model.base.AppResponse;
import com.be.model.base.PageResponse;
import com.be.model.dto.*;
import com.be.model.entity.CategoryType;
import com.be.model.entity.MovieRating;
import com.be.service.MovieService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@Slf4j
public class MovieController {
    private final HttpServletRequest request;
    private final MovieService movieService;
    private final MovieRecommendationService movieRecommendationService;

    public MovieController(HttpServletRequest request,
                           MovieService movieService,
                           MovieRecommendationService movieRecommendationService) {
        this.request = request;
        this.movieService = movieService;
        this.movieRecommendationService = movieRecommendationService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppResponse<Void>> handleException(Exception e) {
        log.error("Error in movie controller: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AppResponse.buildResponse(
                        e.getMessage(),
                        request.getRequestURI(),
                        "Internal Server Error",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        null
                ));
    }

    //    Trending movies by today
    @GetMapping("/trending/day")
    public ResponseEntity<AppResponse<PageResponse<MovieDTO>>> getMoviesTrendingByDay(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MovieDTO> moviePage = movieService.findMovieByCategories(CategoryType.TRENDING_DAY.name(), PageRequest.of(page, size));
        PageResponse<MovieDTO> pageResponse = PageResponse.of(moviePage);

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movies retrieved successfully",
                HttpStatus.OK.value(),
                pageResponse
        ));
    }

    //    Trending movies by this week
    @GetMapping("/trending/week")
    public ResponseEntity<AppResponse<PageResponse<MovieDTO>>> getMoviesTrendingByWeek(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MovieDTO> moviePage = movieService.findMovieByCategories(CategoryType.TRENDING_WEEK.name(), PageRequest.of(page, size));
        PageResponse<MovieDTO> pageResponse = PageResponse.of(moviePage);

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movies retrieved successfully 111111",
                HttpStatus.OK.value(),
                pageResponse
        ));
    }

    @GetMapping("/all")
    public ResponseEntity<AppResponse<PageResponse<MovieDTO>>> getMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MovieDTO> moviePage = movieService.findAll(PageRequest.of(page, size));
        PageResponse<MovieDTO> pageResponse = PageResponse.of(moviePage);

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movies retrieved successfully",
                HttpStatus.OK.value(),
                pageResponse
        ));
    }

    @GetMapping("/{movieId}/trailers")
    public ResponseEntity<AppResponse<PageResponse<MovieTrailerDTO>>> getMovieTrailers(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MovieTrailerDTO> movieTrailerPage = movieService.getMovieTrailers(movieId, PageRequest.of(page, size));
        PageResponse<MovieTrailerDTO> pageResponse = PageResponse.of(movieTrailerPage);
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movie trailers retrieved successfully",
                HttpStatus.OK.value(),
                pageResponse
        ));
    }

    //    Popular movies
    @GetMapping("/popular")
    public ResponseEntity<AppResponse<PageResponse<MovieDTO>>> getMoviesPopular(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MovieDTO> moviePage = movieService.findMovieByCategories(CategoryType.POPULAR.name(), PageRequest.of(page, size));
        PageResponse<MovieDTO> pageResponse = PageResponse.of(moviePage);

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movies retrieved successfully",
                HttpStatus.OK.value(),
                pageResponse
        ));
    }

    // Rating
    @PostMapping("/{movieId}/rating")
    public ResponseEntity<AppResponse<MovieRatingDTO>> rateMovie(
            @PathVariable Long movieId,
            @RequestBody RatingRequest ratingRequest) {

        MovieRating rating = MovieRating.builder().build();
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("rateMovie userId = {}, movieId = {}, rating = {}", userId, movieId, ratingRequest.getRating());
        if (userId != null) {
            ratingRequest.setUserId(userId);
            rating = movieService.rateMovie(movieId, ratingRequest.getUserId(), ratingRequest.getRating());
        }
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Rating added successfully",
                HttpStatus.OK.value(),
                MovieRatingDTO.fromEntity(rating)
        ));
    }

    //    Mark as favorite
    @PostMapping("/{movieId}/favorite")
    public ResponseEntity<AppResponse<Void>> toggleFavorite(
            @PathVariable Long movieId) {

        Long userId = SecurityUtils.getCurrentUserId();
        log.info("toggleFavorite userId = {}, movieId = {}", userId, movieId);
        if (userId != null) {
            movieService.toggleFavorite(movieId, userId);
        }
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Favorite toggled successfully",
                HttpStatus.OK.value(),
                null
        ));
    }

    // Watchlist endpoints
    @PostMapping("/{movieId}/watchlist")
    public ResponseEntity<AppResponse<Void>> toggleWatchlist(
            @PathVariable Long movieId) {

        Long userId = SecurityUtils.getCurrentUserId();
        log.info("toggleWatchlist userId = {}, movieId = {}", userId, movieId);
        if (userId != null) {
            movieService.toggleWatchlist(movieId, userId);
        }
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Watchlist toggle successfully",
                HttpStatus.OK.value(),
                null
        ));
    }

    @DeleteMapping("/{movieId}/watchlist")
    public ResponseEntity<AppResponse<Void>> removeFromWatchlist(
            @PathVariable Long movieId) {

        Long userId = SecurityUtils.getCurrentUserId();
        log.info("removeFromWatchlist userId = {}, movieId = {}", userId, movieId);
        if (userId != null) {
            movieService.removeFromWatchlist(movieId, userId);
        }

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Removed from watchlist successfully",
                HttpStatus.OK.value(),
                null
        ));
    }

    // Cast endpoint
    @GetMapping("/{movieId}/cast")
    public ResponseEntity<AppResponse<Page<CastDTO>>> getMovieCast(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<CastDTO> cast = movieService.getMovieCast(movieId, PageRequest.of(page, size));
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Cast retrieved successfully",
                HttpStatus.OK.value(),
                cast
        ));
    }

    // Reviews endpoints
    @GetMapping("/{movieId}/reviews")
    public ResponseEntity<AppResponse<Page<ReviewDTO>>> getMovieReviews(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReviewDTO> reviews = movieService.getMovieReviews(movieId, PageRequest.of(page, size));
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Reviews retrieved successfully",
                HttpStatus.OK.value(),
                reviews
        ));
    }

    @GetMapping("/trailers/latest")
    public ResponseEntity<AppResponse<Page<MovieTrailerDTO>>> getLatestTrailers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MovieTrailerDTO> trailers = movieService.getLatestTrailers(PageRequest.of(page, size));

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Latest trailers retrieved successfully",
                HttpStatus.OK.value(),
                trailers
        ));
    }

    // TODO  Quick info, Add to watch list, Casts, Reviews

    @GetMapping("/{movieId}/detail")
    public ResponseEntity<AppResponse<MovieDetailDTO>> getMovieDetail(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        MovieDetailDTO movieDetail = movieService.getMovieDetail(movieId, userId, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movie details retrieved successfully",
                HttpStatus.OK.value(),
                movieDetail
        ));
    }

    @GetMapping("/recommendations/user")
    public ResponseEntity<AppResponse<Page<MovieDTO>>> getRecommendationsForUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new ResourceNotFoundException("User must be logged in");
        }

        Page<MovieDTO> recommendations = movieRecommendationService
                .getRecommendationsByUserHistory(userId, PageRequest.of(page, size));

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Recommendations retrieved successfully",
                HttpStatus.OK.value(),
                recommendations
        ));
    }

    @GetMapping("/{movieId}/recommendations")
    public ResponseEntity<AppResponse<Page<MovieDTO>>> getSimilarMovies(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<MovieDTO> recommendations = movieRecommendationService
                .getRecommendationsByVectorSimilarity(movieId, PageRequest.of(page, size));

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Similar movies retrieved successfully",
                HttpStatus.OK.value(),
                recommendations
        ));
    }

//    @GetMapping("/{movieId}/recommendations")
//    public ResponseEntity<AppResponse<Page<MovieDTO>>> getRecommendations(
//            @PathVariable Long movieId,
//            @RequestParam(required = false) Long userId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//        Page<MovieDTO> recommendations;
//        if (userId != null) {
//            recommendations = movieService.getRecommendationsByUserHistory(userId, PageRequest.of(page, size));
//        } else {
//            recommendations = movieService.getSimilarMovies(movieId, PageRequest.of(page, size));
//        }
//        return ResponseEntity.ok(AppResponse.buildResponse(
//                null,
//                request.getRequestURI(),
//                "Recommendations retrieved successfully",
//                HttpStatus.OK.value(),
//                recommendations
//        ));
//    }
}
