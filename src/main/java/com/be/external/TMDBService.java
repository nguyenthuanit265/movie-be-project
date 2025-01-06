package com.be.external;

import com.be.model.dto.tmdb.*;
import com.be.model.entity.Movie;
import com.be.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TMDBService {
    private final String API_KEY = "your_api_key";
    private final String BASE_URL = "https://api.themoviedb.org/3";
    private final RestTemplate restTemplate;
    private final MovieRepository movieRepository;

    public TMDBService(RestTemplate restTemplate, MovieRepository movieRepository) {
        this.restTemplate = restTemplate;
        this.movieRepository = movieRepository;
    }

    public Movie importMovieFromTMDB(Long tmdbId) {
        String url = String.format("%s/movie/%d?api_key=%s", BASE_URL, tmdbId, API_KEY);
        TMDBMovieDTO tmdbMovie = restTemplate.getForObject(url, TMDBMovieDTO.class);

        return Movie.builder()
                .id(tmdbMovie.getId())
                .title(tmdbMovie.getTitle())
                .originalTitle(tmdbMovie.getOriginal_title())
                .overview(tmdbMovie.getOverview())
                .releaseDate(LocalDate.parse(tmdbMovie.getRelease_date()))
                .posterPath(tmdbMovie.getPoster_path())
                .backdropPath(tmdbMovie.getBackdrop_path())
                .popularity(tmdbMovie.getPopularity())
                .voteAverage(tmdbMovie.getVote_average())
                .voteCount(tmdbMovie.getVote_count())
                .build();
    }

    // Multi-search (movies, people, tv shows)
    public SearchResultDTO searchMulti(String query, int page) {
        String url = String.format("%s/search/multi?api_key=%s&query=%s&page=%d",
                BASE_URL, API_KEY, query, page);
        return restTemplate.getForObject(url, SearchResultDTO.class);
    }

    // Movie-specific search
    public SearchResultDTO searchMovies(String query, int page) {
        String url = String.format("%s/search/movie?api_key=%s&query=%s&page=%d",
                BASE_URL, API_KEY, query, page);
        return restTemplate.getForObject(url, SearchResultDTO.class);
    }

    // Get trending movies (day/week)
    public List<TrendingItemDTO> getTrendingMovies(String timeWindow) { // timeWindow: "day" or "week"
        String url = String.format("%s/trending/movie/%s?api_key=%s",
                BASE_URL, timeWindow, API_KEY);
        return restTemplate.getForObject(url, TrendingResponse.class).getResults();
    }

    // Get trending movies by today
    public List<TrendingItemDTO> getTrendingMoviesToday() {
        return getTrendingMovies("day");
    }

    // Get trending movies by week
    public List<TrendingItemDTO> getTrendingMoviesThisWeek() {
        return getTrendingMovies("week");
    }

    // Get movie trailers
    public List<VideoDTO> getMovieTrailers(Long movieId) {
        String url = String.format("%s/movie/%d/videos?api_key=%s",
                BASE_URL, movieId, API_KEY);
        VideoResponse response = restTemplate.getForObject(url, VideoResponse.class);
        return response.getResults().stream()
                .filter(video -> "Trailer".equals(video.getType()))
                .collect(Collectors.toList());
    }

    // Get latest trailers (combine with upcoming movies)
    public List<MovieTrailerDTO> getLatestTrailers() {
        String upcomingUrl = String.format("%s/movie/upcoming?api_key=%s",
                BASE_URL, API_KEY);
        MovieResponse upcoming = restTemplate.getForObject(upcomingUrl, MovieResponse.class);

        return upcoming.getResults().stream()
                .map(movie -> {
                    List<VideoDTO> trailers = getMovieTrailers(movie.getId());
                    return new MovieTrailerDTO(movie, trailers);
                })
                .filter(mt -> !mt.getTrailers().isEmpty())
                .collect(Collectors.toList());
    }

    // Get popular movies
    public List<TMDBMovieDTO> getPopularMovies(int page) {
        String url = String.format("%s/movie/popular?api_key=%s&page=%d",
                BASE_URL, API_KEY, page);
        return restTemplate.getForObject(url, MovieResponse.class).getResults();
    }

    // Get top rated movies
    public List<TMDBMovieDTO> getTopRatedMovies(int page) {
        String url = String.format("%s/movie/top_rated?api_key=%s&page=%d",
                BASE_URL, API_KEY, page);
        return restTemplate.getForObject(url, MovieResponse.class).getResults();
    }
}