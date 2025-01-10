package com.be.model.dto;

import com.be.model.entity.MovieTrailer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieTrailerDTO {
    private Long id;
    private Long movieId;
    private String movieTitle;  // Additional movie info
    private String key;         // YouTube video key
    private String name;
    private String site;        // "YouTube", "Vimeo", etc.
    private String type;        // "Trailer", "Teaser", etc.
    private boolean official;
    private LocalDateTime publishedAt;
    private String posterPath;  // Movie poster for thumbnail

    // Helper method to convert Entity to DTO
    public static MovieTrailerDTO fromEntity(MovieTrailer trailer) {
        return MovieTrailerDTO.builder()
                .id(trailer.getId())
                .movieId(trailer.getMovie().getId())
                .movieTitle(trailer.getMovie().getTitle())
                .key(trailer.getKey())
                .name(trailer.getName())
                .site(trailer.getSite())
                .type(trailer.getType())
                .official(trailer.isOfficial())
                .publishedAt(trailer.getPublishedAt())
                .posterPath(trailer.getMovie().getPosterPath())
                .build();
    }

    // Generate YouTube URL
    public String getYoutubeUrl() {
        if ("YouTube".equals(site)) {
            return "https://www.youtube.com/watch?v=" + key;
        }
        return null;
    }

    // Generate YouTube embed URL
    public String getYoutubeEmbedUrl() {
        if ("YouTube".equals(site)) {
            return "https://www.youtube.com/embed/" + key;
        }
        return null;
    }
}