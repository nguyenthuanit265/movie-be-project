package com.be.model.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TMDBVideoDTO {
    private String id;
    private String key;          // YouTube video key
    private String name;
    private String site;         // "YouTube", "Vimeo", etc.
    private String type;         // "Trailer", "Teaser", etc.
    private boolean official;

    @JsonProperty("published_at")
    private String publishedAt;
}
