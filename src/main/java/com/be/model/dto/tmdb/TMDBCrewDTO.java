package com.be.model.dto.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TMDBCrewDTO {
    private Long id;
    private String name;
    private String department;    // e.g., "Directing", "Writing"
    private String job;          // e.g., "Director", "Screenplay"
    private String profile_path;
    private Integer gender;
    private Double popularity;
}
