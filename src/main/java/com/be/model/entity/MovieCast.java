package com.be.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movie_casts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieCast {
    @EmbeddedId
    private MovieCastId id;

    @ManyToOne
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne
    @MapsId("castId")
    @JoinColumn(name = "cast_id")
    private Cast cast;

    private String role;
}

