package com.be.controller;

import com.be.model.base.AppResponse;
import com.be.model.dto.ReviewRequestDTO;
import com.be.service.ReviewService;
import com.be.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final HttpServletRequest request;

    @PostMapping("/manage")
    public ResponseEntity<?> manageReview(
            @RequestBody ReviewRequestDTO reviewRequestDTO
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Review successfully",
                HttpStatus.OK.value(),
                reviewService.manageReview(reviewRequestDTO, currentUserId)
        ));
    }
}