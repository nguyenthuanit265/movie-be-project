package com.be.service;

import com.be.model.dto.MovieDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieRecommendationService {
    Page<MovieDTO> getRecommendationsByUserHistory(Long userId, Pageable pageable);
    Page<MovieDTO> getRecommendationsByVectorSimilarity(Long movieId, Pageable pageable);
}
