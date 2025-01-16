package com.be.service;

import com.be.model.dto.ReviewRequestDTO;

public interface ReviewService {
    Object manageReview(ReviewRequestDTO request, Long userId);
}
