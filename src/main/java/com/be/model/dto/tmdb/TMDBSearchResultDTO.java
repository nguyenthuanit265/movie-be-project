package com.be.model.dto.tmdb;

import lombok.Data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TMDBSearchResultDTO {
    private int page;
    private List<TMDBSearchResultItemDTO> results;
    private int total_pages;
    private int total_results;
}
