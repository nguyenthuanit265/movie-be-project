package com.be.model.dto;

import com.be.model.entity.MovieRating;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRatingDTO {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("status_message")
    private String statusMessage;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("movie_id")
    private Long movieId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("value")
    private Float value;

    public static MovieRatingDTO fromEntity(MovieRating trailer) {
        return MovieRatingDTO.builder()
                .id(trailer.getId())
                .success(true)
                .movieId(trailer.getMovie().getId())
                .movieId(trailer.getUser().getId())
                .build();
    }
}
