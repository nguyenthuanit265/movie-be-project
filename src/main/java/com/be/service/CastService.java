package com.be.service;

import com.be.model.dto.CastDetailDTO;
import com.be.model.dto.MovieDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CastService {
    CastDetailDTO getCastDetails(Long castId);
    Page<MovieDTO> getCastMovies(Long castId, Pageable pageable);
}
