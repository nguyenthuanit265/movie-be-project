package com.be.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDTO {
    private String action; // "add", "update", or "delete"

    @NotNull(message = "Movie ID is required")
    @JsonProperty("movie_id")
    private Long movieId;

    private String content;

    @Min(value = 0, message = "Rating must be between 0 and 10")
    @Max(value = 10, message = "Rating must be between 0 and 10")
    private Float rating;

    @JsonProperty("tmdb_id")
    private String tmdbId;
}
