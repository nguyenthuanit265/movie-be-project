package com.be.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionCompanyDTO {
    private Long id;
    private Long tmdbId;
    private String name;
    private String logoPath;
    private String originCountry;
}