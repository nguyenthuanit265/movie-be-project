package com.be.model.searchdto;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SearchMovieResponse {
    private Long id;
    private String title;
    private String originalTitle;
    private String overview;
    private String posterPath;
    private String posterUrl;
    private String backdropPath;
    private LocalDate releaseDate;
    private Float voteAverage;
    private Integer voteCount;
    private Float popularity;
    private List<String> genreNames;
}