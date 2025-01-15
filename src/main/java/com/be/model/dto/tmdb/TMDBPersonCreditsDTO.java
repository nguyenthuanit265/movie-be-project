package com.be.model.dto.tmdb;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TMDBPersonCreditsDTO {
    private List<TMDBPersonCastDTO> cast;
    private List<TMDBPersonCrewDTO> crew;
}
