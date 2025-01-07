package com.be.service.impl;

import com.be.model.dto.GenreDTO;
import com.be.model.dto.MovieDTO;
import com.be.model.entity.Movie;
import com.be.repository.MovieRepository;
import com.be.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public MovieDTO toDTO(Movie movie) {
        if (movie == null) return null;

        return MovieDTO.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .runtime(movie.getRuntime())
                .posterPath(movie.getPosterPath())
                .backdropPath(movie.getBackdropPath())
                .popularity(movie.getPopularity())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .genres(movie.getGenres() != null ?
                        movie.getGenres().stream()
                                .map(genre -> GenreDTO.builder()
                                        .id(genre.getId())
                                        .name(genre.getName())
                                        .build())
                                .collect(Collectors.toSet()) :
                        null)
                .build();
    }

    public List<MovieDTO> toDTOList(List<Movie> movies) {
        return movies.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Page<MovieDTO> toDTOPage(Page<Movie> moviePage) {
        return moviePage.map(this::toDTO);
    }

    public Page<Movie> searchMovies(String query, int page) {
        return movieRepository.search(
                query,
                PageRequest.of(page - 1, 20)
        );
    }


    @Transactional(readOnly = true)  // Add this
    public Page<MovieDTO> findAll(Pageable pageable) {
        return toDTOPage(movieRepository.findAll(pageable));
    }
}
