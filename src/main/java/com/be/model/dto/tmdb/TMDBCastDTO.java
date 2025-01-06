package com.be.model.dto.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TMDBCastDTO {
    private Long id;              // person/cast id
    private String name;
    private String character;     // role name in the movie
    private Integer order;        // order in credits
    private String profile_path;  // actor's profile image
    private String known_for_department;
    private Integer gender;       // 1: Woman, 2: Man
    private Double popularity;
}
