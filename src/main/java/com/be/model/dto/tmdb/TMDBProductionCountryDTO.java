package com.be.model.dto.tmdb;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TMDBProductionCountryDTO {
    private String iso_3166_1;
    private String name;
}