package com.be.model.dto.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TMDBCreditsDTO {
    private Long id;  // movie id
    private List<TMDBCastDTO> cast;
    private List<TMDBCrewDTO> crew;
}