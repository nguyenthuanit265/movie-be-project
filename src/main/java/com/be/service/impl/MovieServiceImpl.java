package com.be.service.impl;

import com.be.model.dto.GenreDTO;
import com.be.model.dto.MovieDTO;
import com.be.model.dto.MovieTrailerDTO;
import com.be.model.entity.Movie;
import com.be.model.entity.MovieTrailer;
import com.be.repository.MovieRepository;
import com.be.repository.MovieTrailerRepository;
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
    private final MovieTrailerRepository movieTrailerRepository;

    public MovieServiceImpl(MovieRepository movieRepository,
                            MovieTrailerRepository movieTrailerRepository) {
        this.movieRepository = movieRepository;
        this.movieTrailerRepository = movieTrailerRepository;
    }

    public MovieDTO toMovieDTO(Movie movie) {
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
                .backdropUrl(movie.getBackdropUrl())
                .posterUrl(movie.getPosterUrl())
                .build();
    }

    public List<MovieDTO> toDTOList(List<Movie> movies) {
        return movies.stream()
                .map(this::toMovieDTO)
                .collect(Collectors.toList());
    }

    public Page<MovieDTO> toMovieDTOPage(Page<Movie> moviePage) {
        return moviePage.map(this::toMovieDTO);
    }

    public Page<Movie> searchMovies(String query, int page) {
        return movieRepository.search(
                query,
                PageRequest.of(page - 1, 20)
        );
    }


    @Transactional(readOnly = true)  // Add this
    public Page<MovieDTO> findAll(Pageable pageable) {
        return toMovieDTOPage(movieRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)  // Add this
    public Page<MovieDTO> findMovieByCategories(String category, Pageable pageable) {
        return toMovieDTOPage(movieRepository.findMovieByCategory(category, pageable));
    }

    public List<MovieTrailerDTO> getMovieTrailers(Long movieId) {
        List<MovieTrailer> trailers = movieTrailerRepository.findByMovieIdOrderByPublishedAtDesc(movieId);
        return trailers.stream()
                .map(MovieTrailerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<MovieTrailerDTO> getMovieTrailers(Long movieId, Pageable pageable) {
        Page<MovieTrailer> trailers = movieTrailerRepository.findByMovieIdOrderByPublishedAtDesc(movieId, pageable);
        return trailers.map(MovieTrailerDTO::fromEntity);
    }
}
