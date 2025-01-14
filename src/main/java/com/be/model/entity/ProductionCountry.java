package com.be.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ProductionCountry {
    @Column(name = "iso_3166_1")
    private String iso31661;

    private String name;
}
