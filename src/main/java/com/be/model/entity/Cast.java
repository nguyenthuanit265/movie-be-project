package com.be.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "casts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cast extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tmdb_id", unique = true)
    private Long tmdbId;

    @Column(nullable = false)
    private String name;

    @Column(name = "profile_path")
    private String profilePath;

    private String biography;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Column(name = "known_for_department")
    private String knownForDepartment;

    private Float popularity;

    private String gender;  // Could be an enum if needed

    @Column(name = "imdb_id")
    private String imdbId;

    @OneToMany(mappedBy = "cast")
    private Set<MovieCast> movies;
}