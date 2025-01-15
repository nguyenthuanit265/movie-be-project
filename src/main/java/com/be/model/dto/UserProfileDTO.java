package com.be.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String email;
    private String fullName;
    private String provider;
    private String providerId;
    private String imageUrl;
    private String role;

    // Stats
    private Integer totalWatchlist;
    private Integer totalFavorites;
    private Integer totalRatings;
    private Float averageRating;

    // Recent activity
    private List<MovieDTO> recentWatchlist;
    private List<MovieDTO> recentFavorites;
    private List<UserRatingDTO> recentRatings;
}
