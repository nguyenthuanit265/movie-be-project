package com.be.model.dto.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TMDBMovieTrailerDTO {
    private TMDBMovieDTO movie;
    private List<TMDBVideoDTO> trailers;
}
