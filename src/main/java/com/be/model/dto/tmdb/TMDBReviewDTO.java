package com.be.model.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TMDBReviewDTO {
    private String id;
    private String author;
    @JsonProperty("author_details")
    private TMDBAuthorDTO authorDetails;
    private String content;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    private String url;
}