package com.be.model.searchdto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class KnownForMovie {
    private Long id;
    private String title;
    private String posterPath;
    private LocalDate releaseDate;
    private Float voteAverage;
}