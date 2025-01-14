package com.be.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieCollectionDTO {
    private Long id;
    private String name;
    private String posterPath;
    private String backdropPath;
}
