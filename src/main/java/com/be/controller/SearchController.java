package com.be.controller;

import com.be.model.base.AppResponse;
import com.be.model.base.PageResponse;
import com.be.model.searchdto.MultiSearchResponse;
import com.be.model.searchdto.SearchMovieResponse;
import com.be.model.searchdto.SearchPersonResponse;
import com.be.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Builder
public class SearchController {

    private final SearchService searchService;
    private final HttpServletRequest request;

    @GetMapping("/movie")
    public ResponseEntity<AppResponse<PageResponse<SearchMovieResponse>>> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) List<Integer> genreIds,
            @RequestParam(defaultValue = "false") boolean includeAdult
    ) {
        PageResponse<SearchMovieResponse> res = searchService.searchMovies(query, page, size, year, genreIds, includeAdult);
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movies retrieved successfully",
                HttpStatus.OK.value(),
                res
        ));
    }

    @GetMapping("/person")
    public ResponseEntity<AppResponse<PageResponse<SearchPersonResponse>>> searchPeople(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResponse<SearchPersonResponse> res = searchService.searchPeople(query, page, size);
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movies retrieved successfully",
                HttpStatus.OK.value(),
                res
        ));
    }

    @GetMapping("/multi")
    public ResponseEntity<AppResponse<PageResponse<MultiSearchResponse>>> multiSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean includeAdult
    ) {
        PageResponse<MultiSearchResponse> res = searchService.multiSearch(query, page, size, includeAdult);
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Movies retrieved successfully",
                HttpStatus.OK.value(),
                res
        ));
    }
}
