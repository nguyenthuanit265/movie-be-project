package com.be.controller;

import com.be.model.base.AppResponse;
import com.be.model.dto.CastDetailDTO;
import com.be.model.dto.MovieDTO;
import com.be.service.CastService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/casts")
@Slf4j
public class CastController {
    private final HttpServletRequest request;
    private final CastService castService;

    public CastController(HttpServletRequest request, CastService castService) {
        this.request = request;
        this.castService = castService;
    }

    @GetMapping("/{castId}/detail")
    public ResponseEntity<AppResponse<CastDetailDTO>> getCastDetails(
            @PathVariable Long castId) {
        CastDetailDTO castDetails = castService.getCastDetails(castId);

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Cast details retrieved successfully",
                HttpStatus.OK.value(),
                castDetails
        ));
    }

    @GetMapping("/{castId}/movies")
    public ResponseEntity<AppResponse<Page<MovieDTO>>> getCastMovies(
            @PathVariable Long castId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MovieDTO> movies = castService.getCastMovies(castId, PageRequest.of(page, size));

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Cast movies retrieved successfully",
                HttpStatus.OK.value(),
                movies
        ));
    }
}
