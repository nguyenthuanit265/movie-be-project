package com.be.model.searchdto;

import lombok.Data;

import java.util.List;

@Data
public class SearchPersonResponse {
    private Long id;
    private String name;
    private String profilePath;
    private Float popularity;
    private String knownForDepartment;
    private List<KnownForMovie> knownFor;
}