package com.be.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "movie_vectors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieVector extends BaseEntity {
    @Id
    private Long movieId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @JdbcTypeCode(SqlTypes.OTHER)
    @Column(name = "embedding", columnDefinition = "vector(384)")
    private double[] embedding; // Changed to double[] from Float[]
}