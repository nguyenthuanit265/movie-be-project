package com.be.model.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultItemDTO {
    private Long id;
    private String title;
    private String name;                    // For TV shows and people
    private String originalTitle;
    private String mediaType;               // movie, tv, person
    private String overview;
    private String posterPath;
    private String backdropPath;
    private String profilePath;             // For people
    private String releaseDate;
    private String firstAirDate;            // For TV shows
    private Float popularity;
    private Float voteAverage;
    private Integer voteCount;
    private List<Integer> genreIds;
    private String originalLanguage;
    private Boolean adult;
    private Boolean video;                  // Indicates if movie has video

    @JsonProperty("media_type")
    public String getMediaType() {
        return mediaType;
    }

    @JsonProperty("poster_path")
    public String getPosterPath() {
        return posterPath;
    }

    @JsonProperty("backdrop_path")
    public String getBackdropPath() {
        return backdropPath;
    }

    @JsonProperty("profile_path")
    public String getProfilePath() {
        return profilePath;
    }

    @JsonProperty("release_date")
    public String getReleaseDate() {
        return releaseDate;
    }

    @JsonProperty("first_air_date")
    public String getFirstAirDate() {
        return firstAirDate;
    }

    @JsonProperty("vote_average")
    public Float getVoteAverage() {
        return voteAverage;
    }

    @JsonProperty("vote_count")
    public Integer getVoteCount() {
        return voteCount;
    }

    @JsonProperty("genre_ids")
    public List<Integer> getGenreIds() {
        return genreIds;
    }

    @JsonProperty("original_language")
    public String getOriginalLanguage() {
        return originalLanguage;
    }
}
