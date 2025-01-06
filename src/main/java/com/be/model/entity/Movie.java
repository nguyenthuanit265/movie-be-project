package com.be.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "original_title")
    private String originalTitle;

    private String overview;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private Float runtime;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "backdrop_path")
    private String backdropPath;

    private Float popularity = 0f;

    @Column(name = "vote_average")
    private Float voteAverage = 0f;

    @Column(name = "vote_count")
    private Integer voteCount = 0;

    @ManyToMany
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @OneToMany(mappedBy = "movie")
    private Set<MovieCast> casts;

    @OneToOne(mappedBy = "movie")
    private MovieVector movieVector;
}