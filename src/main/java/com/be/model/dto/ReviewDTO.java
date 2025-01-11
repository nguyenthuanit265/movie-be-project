package com.be.model.dto;

import com.be.model.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long userId;
    private String userName;
    private String content;
    private Float rating;
    private Integer likes;
    private String createdAt;  // Formatted date string
    private String updatedAt;  // Formatted date string

    // Helper method to convert Entity to DTO
    public static ReviewDTO fromEntity(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .movieId(review.getMovie().getId())
                .movieTitle(review.getMovie().getTitle())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .content(review.getContent())
                .rating(review.getRating())
                .likes(review.getLikes())
                .createdAt(review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .updatedAt(review.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}