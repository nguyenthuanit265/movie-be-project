package com.be.model.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TMDBPersonCrewDTO {
    private Long id;
    private String title;
    private String department;
    private String job;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("poster_path")
    private String posterPath;
    private Boolean adult;
    private String overview;
    @JsonProperty("original_title")
    private String originalTitle;
    private Float popularity;
    @JsonProperty("vote_average")
    private Float voteAverage;
    @JsonProperty("vote_count")
    private Integer voteCount;
    @JsonProperty("credit_id")
    private String creditId;
    private String genre_ids;
}
