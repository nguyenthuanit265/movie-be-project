package com.be.model.searchdto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MultiSearchResponse {
    private Long id;
    private String mediaType;
    private String title;
    private String name;
    private String overview;
    private String profilePath;
    private String posterPath;
    private String backdropPath;
    private Float popularity;
    private LocalDate releaseDate;
    private Float voteAverage;
}
