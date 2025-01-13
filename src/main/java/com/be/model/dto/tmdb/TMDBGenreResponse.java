package com.be.model.dto.tmdb;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TMDBGenreResponse {
    private List<TMDBGenreDTO> genres;
}
