package com.be.service.impl;

import com.be.model.dto.ReviewRequest;
import com.be.model.dto.ReviewRequestDTO;
import com.be.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public Object manageReview(ReviewRequestDTO request, Long userId) {
        switch (request.getAction().toLowerCase()) {
            case "add":
                return addReview(request, userId);
            case "update":
                return updateReview(request, userId);
            case "delete":
                return deleteReview(request, userId);
            default:
                throw new IllegalArgumentException("Invalid action: " + request.getAction());
        }
    }

    private Object addReview(ReviewRequestDTO request, Long userId) {
        // Check if review already exists
        String checkSql = "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND movie_id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, request.getMovieId());

        if (count > 0) {
            throw new IllegalStateException("Review already exists for this movie");
        }

        // Add new review
        String sql = """
                    INSERT INTO reviews (user_id, movie_id, content, rating, tmdb_id, created_at, updated_at)
                    VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                    RETURNING id, content, rating, created_at
                """;

        return jdbcTemplate.queryForMap(sql,
                userId,
                request.getMovieId(),
                request.getContent(),
                request.getRating(),
                request.getTmdbId()
        );
    }

    private Object updateReview(ReviewRequestDTO request, Long userId) {
        // Check if review exists and belongs to user
        String checkSql = "SELECT id FROM reviews WHERE user_id = ? AND movie_id = ?";
        Long reviewId = jdbcTemplate.queryForObject(checkSql, Long.class, userId, request.getMovieId());

        if (reviewId == null) {
            throw new IllegalStateException("Review not found or unauthorized");
        }

        // Update review
        String sql = """
                    UPDATE reviews 
                    SET content = ?, 
                        rating = ?,
                        updated_at = CURRENT_TIMESTAMP
                    WHERE id = ?
                    RETURNING id, content, rating, updated_at
                """;

        return jdbcTemplate.queryForMap(sql,
                request.getContent(),
                request.getRating(),
                reviewId
        );
    }

    private Object deleteReview(ReviewRequestDTO request, Long userId) {
        // Check if review exists and belongs to user
        String checkSql = "SELECT id FROM reviews WHERE user_id = ? AND movie_id = ?";
        Long reviewId = jdbcTemplate.queryForObject(checkSql, Long.class, userId, request.getMovieId());

        if (reviewId == null) {
            throw new IllegalStateException("Review not found or unauthorized");
        }

        // Delete review
        String sql = "DELETE FROM reviews WHERE id = ? RETURNING id";
        return jdbcTemplate.queryForMap(sql, reviewId);
    }
}
