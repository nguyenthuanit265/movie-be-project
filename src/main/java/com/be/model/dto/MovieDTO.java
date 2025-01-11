package com.be.model.dto;

import com.be.model.entity.Movie;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private Long id;

    @JsonProperty("tmdb_id")
    private Long tmdbId;

    private String title;

    @JsonProperty("original_title")
    private String originalTitle;

    private String overview;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    private Float runtime;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    private Float popularity;

    @JsonProperty("vote_average")
    private Float voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;

    private Set<GenreDTO> genres;

    @JsonProperty("poster_url")
    private String posterUrl;

    @JsonProperty("backdrop_url")
    private String backdropUrl;

    public static MovieDTO fromEntity(Movie movie) {
        return MovieDTO.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .runtime(movie.getRuntime())
                .posterPath(movie.getPosterPath())
                .backdropPath(movie.getBackdropPath())
                .popularity(movie.getPopularity())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .genres(movie.getGenres() != null ?
                        movie.getGenres().stream()
                                .map(GenreDTO::fromEntity)
                                .collect(Collectors.toSet()) :
                        new HashSet<>())
                .posterUrl(movie.getPosterUrl())
                .backdropUrl(movie.getBackdropUrl())
                .build();
    }
}