package com.be.model.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendingItemDTO {
    private Long id;
    private String title;
    private String name;                    // For TV shows
    private String originalTitle;
    @JsonProperty("original_title")
    private String originalName;            // For TV shows
    private String mediaType;               // movie, tv
    private String overview;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("backdrop_path")
    private String backdropPath;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("first_air_date")
    private String firstAirDate;            // For TV shows
    private Float popularity;
    @JsonProperty("vote_average")
    private Float voteAverage;
    @JsonProperty("vote_count")
    private Integer voteCount;
    @JsonProperty("genre_ids")
    private List<Integer> genreIds;
    @JsonProperty("original_language")
    private String originalLanguage;
    private Boolean adult;
    private Boolean video;
}
