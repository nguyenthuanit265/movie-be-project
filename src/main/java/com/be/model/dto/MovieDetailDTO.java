package com.be.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
