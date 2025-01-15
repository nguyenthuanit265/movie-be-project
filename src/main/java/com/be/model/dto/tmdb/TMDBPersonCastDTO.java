package com.be.model.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TMDBPersonCastDTO {
    private Long id;
    private String title;
    private String character;
    @JsonProperty("release_date")
    private String release_date;
    @JsonProperty("poster_path")
    private String poster_path;
    private Boolean adult;
    @JsonProperty("backdrop_path")
    private String backdrop_path;
    @JsonProperty("genre_ids")
    private List<Integer> genre_ids;
    @JsonProperty("original_language")
    private String original_language;
    @JsonProperty("original_title")
    private String original_title;
    private String overview;
    private Float popularity;
    private Boolean video;
    @JsonProperty("vote_average")
    private Float vote_average;
    @JsonProperty("vote_count")
    private Integer vote_count;
    @JsonProperty("credit_id")
    private String credit_id;
    private Integer order;
}
