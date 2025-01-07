package com.be.model.dto.tmdb;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TMDBVideoResponse {
    private Long id;
    private List<TMDBVideoDTO> results;
}
