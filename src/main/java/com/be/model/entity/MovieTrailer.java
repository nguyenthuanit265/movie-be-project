package com.be.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "movie_trailers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieTrailer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private String key;  // YouTube video key
    private String name;
    private String site;  // "YouTube", "Vimeo", etc.
    private String type;  // "Trailer", "Teaser", etc.
    private boolean official;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}