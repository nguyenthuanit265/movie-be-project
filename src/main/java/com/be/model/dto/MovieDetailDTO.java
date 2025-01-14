package com.be.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDetailDTO {
    private Long id;
    private Long tmdbId;
    private String title;
    private String originalTitle;
    private String overview;
    private LocalDate releaseDate;
    private Float runtime;
    private String posterPath;
    private String backdropPath;
    private String posterUrl;
    private String backdropUrl;
    private Float popularity;
    private Float voteAverage;
    private Integer voteCount;
    private Set<GenreDTO> genres;
    private Set<MovieCastDTO> casts;
    private Set<MovieTrailerDTO> trailers;

    // Additional stats for logged-in user
    private Boolean isFavorite;
    private Boolean isInWatchlist;
    private Float userRating;

    // New fields
    private Boolean adult;
    private String belongsToCollection;
    private Long budget;
    private String homepage;
    private String imdbId;
    private String originalLanguage;
    private Set<String> originCountries;
    private Long revenue;
    private String status;
    private String tagline;
    private Set<ProductionCompanyDTO> productionCompanies;
    private Set<ProductionCountryDTO> productionCountries;
    private Set<SpokenLanguageDTO> spokenLanguages;

    private Float averageRating;
    private Integer totalReviews;
    private Page<ReviewDTO> reviews;
}
