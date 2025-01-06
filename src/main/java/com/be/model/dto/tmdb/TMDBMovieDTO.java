package com.be.model.dto.tmdb;

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
    private String original_title;
    private String overview;
    private String release_date;
    private Integer runtime;
    private String poster_path;
    private String backdrop_path;
    private Float popularity;
    private Float vote_average;
    private Integer vote_count;
    private List<TMDBGenreDTO> genres;
    private List<TMDBCastDTO> credits;
}
