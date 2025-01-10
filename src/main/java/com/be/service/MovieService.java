package com.be.service;

import com.be.model.dto.MovieDTO;
import com.be.model.dto.MovieTrailerDTO;
import com.be.model.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface MovieService {
    Page<Movie> searchMovies(String query, int page);

    Page<MovieDTO> findAll(Pageable pageable);
    Page<MovieDTO> findMovieByCategories(String category, Pageable pageable);
    List<MovieTrailerDTO> getMovieTrailers(Long movieId);
    Page<MovieTrailerDTO> getMovieTrailers(Long movieId, Pageable pageable);
}
