package com.be.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
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

    @Column(name = "tmdb_id", unique = true)  // Add this field
    private Long tmdbId;

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

    @ManyToMany(fetch = FetchType.LAZY)
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

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<MovieCategory> categories;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "backdrop_url")
    private String backdropUrl;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MovieTrailer> movieTrailers = new HashSet<>();

    @OneToMany(mappedBy = "movie")
    private Set<MovieRating> ratings;

    @ManyToMany(mappedBy = "favorites")
    private Set<User> favoritedBy;

    @ManyToMany(mappedBy = "watchlist")
    private Set<User> watchlistedBy;

    @OneToMany(mappedBy = "movie")
    private Set<Review> reviews;

    private Boolean adult;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "collection_id")),
            @AttributeOverride(name = "name", column = @Column(name = "collection_name")),
            @AttributeOverride(name = "posterPath", column = @Column(name = "collection_poster_path")),
            @AttributeOverride(name = "backdropPath", column = @Column(name = "collection_backdrop_path"))
    })
    private MovieCollection collection;

    private Long budget;

    private String homepage;

    @Column(name = "imdb_id")
    private String imdbId;

    @Column(name = "original_language")
    private String originalLanguage;

    @ElementCollection
    @CollectionTable(name = "movie_origin_countries",
            joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "country_code")
    private Set<String> originCountries;

    private Long revenue;

    private String status;

    private String tagline;

    @OneToMany(mappedBy = "movie")
    private Set<MovieProductionCompany> productionCompanies;

    @ElementCollection
    @CollectionTable(name = "movie_production_countries",
            joinColumns = @JoinColumn(name = "movie_id"))
    private Set<ProductionCountry> productionCountries;

    @ElementCollection
    @CollectionTable(name = "movie_spoken_languages",
            joinColumns = @JoinColumn(name = "movie_id"))
    private Set<SpokenLanguage> spokenLanguages;



    public boolean isInCategory(CategoryType category) {
        return categories.stream()
                .anyMatch(mc -> Objects.equals(mc.getCategory(), category.name()));
    }

    // Helper methods to manage the relationship
    public void addTrailer(MovieTrailer trailer) {
        movieTrailers.add(trailer);
        trailer.setMovie(this);
    }

    public void removeTrailer(MovieTrailer trailer) {
        movieTrailers.remove(trailer);
        trailer.setMovie(null);
    }

    public Float getAverageRating() {
        if (ratings == null || ratings.isEmpty()) return 0f;
        return (float) ratings.stream()
                .mapToDouble(MovieRating::getValue)
                .average()
                .orElse(0.0);
    }
}