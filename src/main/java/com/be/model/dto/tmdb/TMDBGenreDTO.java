package com.be.model.dto.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TMDBGenreDTO {
    private Long id;
    private String name;
}