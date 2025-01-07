package com.be.model.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TMDBMovieDTO {
    private Long id;
    private String title;
    @JsonProperty("original_title")
    private String originalTitle;
    private String overview;
    @JsonProperty("release_date")
    private String releaseDate;
    private Integer runtime;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("backdrop_path")
    private String backdropPath;
    private Float popularity;
    @JsonProperty("vote_average")
    private Float voteAverage;
    @JsonProperty("vote_count")
    private Integer voteCount;
    private List<TMDBGenreDTO> genres;
    private Boolean adult;
    @JsonProperty("original_language")
    private String originalLanguage;
    private String status;
    private String tagline;
    private Boolean video;
    @JsonProperty("imdb_id")
    private String imdbId;
}
