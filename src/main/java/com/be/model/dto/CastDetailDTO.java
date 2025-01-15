package com.be.model.dto;

import com.be.model.entity.Cast;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CastDetailDTO {
    private Long id;
    private Long tmdbId;
    private String name;
    private String profilePath;
    private String biography;
    private LocalDate birthDate;
    private String placeOfBirth;
    private String knownForDepartment;
    private Float popularity;
    private String gender;
    private String imdbId;
    private List<MovieDTO> knownFor;  // Top movies

    public static CastDetailDTO fromEntity(Cast cast) {
        return CastDetailDTO.builder()
                .id(cast.getId())
                .tmdbId(cast.getTmdbId())
                .name(cast.getName())
                .profilePath(cast.getProfilePath())
                .biography(cast.getBiography())
                .birthDate(cast.getBirthDate())
                .placeOfBirth(cast.getPlaceOfBirth())
                .knownForDepartment(cast.getKnownForDepartment())
                .popularity(cast.getPopularity())
                .gender(cast.getGender())
                .imdbId(cast.getImdbId())
                .knownFor(cast.getMovies().stream()
                        .map(mc -> MovieDTO.fromEntity(mc.getMovie(), null))
                        .sorted(Comparator.comparing(m -> m.getPopularity(), Comparator.reverseOrder()))
                        .limit(5)
                        .collect(Collectors.toList()))
                .build();
    }
}
