package com.be.controller;


import com.be.model.base.AppResponse;
import com.be.model.base.PageResponse;
import com.be.model.dto.MovieDTO;
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
    private final HttpServletRequest request;  // For getting request path
    private final MovieService movieService;

    public MovieController(HttpServletRequest request, MovieService movieService) {
        this.request = request;
        this.movieService = movieService;
    }

//    @GetMapping("/trending/today")
//    public ResponseEntity<List<MovieDTO>> getTrendingToday() {
//        List<Movie> movies = movieService.getTrendingMoviesToday();
//        return ResponseEntity.ok(convertToDTO(movies));
//    }
//
//    @GetMapping("/trending/week")
//    public ResponseEntity<List<MovieDTO>> getTrendingThisWeek() {
//        List<Movie> movies = movieService.getTrendingMoviesThisWeek();
//        return ResponseEntity.ok(convertToDTO(movies));
//    }

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
}
