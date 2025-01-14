package com.be.model.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class MovieCollection {
    private Long id;
    private String name;
    private String posterPath;
    private String backdropPath;
}
