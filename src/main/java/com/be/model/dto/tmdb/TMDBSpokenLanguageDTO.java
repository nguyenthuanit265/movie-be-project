package com.be.model.dto.tmdb;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TMDBSpokenLanguageDTO {
    private String english_name;
    private String iso_639_1;
    private String name;
}
