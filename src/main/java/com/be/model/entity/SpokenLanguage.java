package com.be.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class SpokenLanguage {
    @Column(name = "english_name")
    private String englishName;

    @Column(name = "iso_639_1")
    private String iso6391;

    private String name;
}
