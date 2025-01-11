package com.be.controller;


import com.be.model.base.AppResponse;
import com.be.model.base.PageResponse;
import com.be.model.dto.MovieDTO;
import com.be.model.dto.MovieRatingDTO;
import com.be.model.dto.MovieTrailerDTO;
import com.be.model.dto.RatingRequest;
import com.be.model.entity.CategoryType;
import com.be.model.entity.MovieRating;
import com.be.service.MovieService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@Slf4j
public class MovieController {
    private final HttpServletRequest request;
    private final MovieService movieService;

    public MovieController(HttpServletRequest request, MovieService movieService) {
        this.request = request;
        this.movieService = movieService;
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
                "Movies retrieved successfully",
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
//        List<MovieTrailerDTO> trailers = movieService.getMovieTrailers(movieId);
//
//        return ResponseEntity.ok(AppResponse.buildResponse(
//                null,
//                request.getRequestURI(),
//                "Movie trailers retrieved successfully",
//                HttpStatus.OK.value(),
//                trailers
//        ));
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
        MovieRating rating = movieService.rateMovie(movieId, ratingRequest.getUserId(), ratingRequest.getRating());
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
            @PathVariable Long movieId,
            @RequestParam Long userId) {
        movieService.toggleFavorite(movieId, userId);
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Favorite toggled successfully",
                HttpStatus.OK.value(),
                null
        ));
    }

    // TODO Latest trailers, Quick info, Add to watch list, Casts, Reviews
}
