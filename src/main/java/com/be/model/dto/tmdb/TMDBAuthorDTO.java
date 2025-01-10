package com.be.model.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TMDBAuthorDTO {
    private String name;
    private String username;
    @JsonProperty("avatar_path")
    private String avatarPath;
    private Float rating;
}