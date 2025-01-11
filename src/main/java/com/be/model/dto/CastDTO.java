package com.be.model.dto;

import com.be.model.entity.MovieCast;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CastDTO {
    private Long movieId;
    private String movieTitle;
    private Long castId;
    private String name;
    private String character;
    private String profilePath;
    private String biography;
    private LocalDate birthDate;

    public static CastDTO fromEntity(MovieCast movieCast) {
        return CastDTO.builder()
                .movieId(movieCast.getMovie().getId())
                .movieTitle(movieCast.getMovie().getTitle())
                .castId(movieCast.getCast().getId())
                .name(movieCast.getCast().getName())
                .character(movieCast.getId().getCharacter())
                .profilePath(movieCast.getCast().getProfilePath())
                .biography(movieCast.getCast().getBiography())
                .birthDate(movieCast.getCast().getBirthDate())
                .build();
    }
}
