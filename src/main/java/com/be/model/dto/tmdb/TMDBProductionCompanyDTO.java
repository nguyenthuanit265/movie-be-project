package com.be.model.dto.tmdb;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TMDBProductionCompanyDTO {
    private Long id;
    private String name;
    private String logo_path;
    private String origin_country;
}
