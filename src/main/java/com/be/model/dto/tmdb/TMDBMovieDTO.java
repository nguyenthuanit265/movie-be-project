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
    @JsonProperty("original_language")
    private String originalLanguage;
    private String status;
    private String tagline;
    private Boolean video;

    // New fields
    private Boolean adult;

    @JsonProperty("belongs_to_collection")
    private TMDBCollectionDTO belongs_to_collection;
    private Long budget;
    private String homepage;

    @JsonProperty("imdb_id")
    private String imdbId;

    @JsonProperty("origin_country")
    private List<String> originCountry;
    private Long revenue;

    private List<TMDBProductionCompanyDTO> production_companies;
    private List<TMDBProductionCountryDTO> production_countries;
    private List<TMDBSpokenLanguageDTO> spoken_languages;
}
