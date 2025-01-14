package com.be.model.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TMDBCollectionDTO {
    private Long id;
    private String name;
    @JsonProperty("poster_path")
    private String poster_path;
    @JsonProperty("backdrop_path")
    private String backdrop_path;
}
