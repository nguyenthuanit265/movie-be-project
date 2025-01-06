package com.be.model.dto.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {
    private String id;
    private String key;          // YouTube video key
    private String name;
    private String site;         // "YouTube", "Vimeo", etc.
    private String type;         // "Trailer", "Teaser", etc.
    private boolean official;
    private String published_at;
}
