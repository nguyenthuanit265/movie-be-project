package com.be.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieCastId implements java.io.Serializable {
    private Long movieId;
    private Long castId;
    private String character;
}
