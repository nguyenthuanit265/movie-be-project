package com.be.model.dto;

import com.be.model.entity.MovieCast;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieCastDTO {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long castId;
    private String name;
    private String character;
    private String profilePath;
    private String role;

    public static MovieCastDTO fromEntity(MovieCast movieCast) {
        return MovieCastDTO.builder()
                .id(movieCast.getId().getCastId())  // Since using composite key
                .movieId(movieCast.getMovie().getId())
                .movieTitle(movieCast.getMovie().getTitle())
                .castId(movieCast.getCast().getId())
                .name(movieCast.getCast().getName())
                .character(movieCast.getId().getCharacter())  // Character is part of composite key
                .profilePath(movieCast.getCast().getProfilePath())
                .role(movieCast.getRole())
                .build();
    }
}