package com.be.service;

import com.be.model.dto.MovieDTO;
import com.be.model.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MovieService {
    Page<Movie> searchMovies(String query, int page);

    Page<MovieDTO> findAll(Pageable pageable);
}
