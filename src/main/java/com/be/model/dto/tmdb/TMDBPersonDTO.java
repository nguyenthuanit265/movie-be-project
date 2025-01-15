package com.be.model.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TMDBPersonDTO {
    private Long id;
    private String name;
    @JsonProperty("profile_path")
    private String profilePath;
    private String biography;
    @JsonProperty("birthday")
    private String birthDate;
    @JsonProperty("place_of_birth")
    private String placeOfBirth;
    @JsonProperty("known_for_department")
    private String knownForDepartment;
    private Float popularity;
    private Integer gender;
    @JsonProperty("imdb_id")
    private String imdbId;
    @JsonProperty("movie_credits")
    private TMDBPersonCreditsDTO movieCredits;
}
