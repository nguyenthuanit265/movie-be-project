package com.be.service.external;

import com.be.appexception.ResourceNotFoundException;
import com.be.model.dto.tmdb.*;
import com.be.model.entity.*;
import com.be.repository.CastRepository;
import com.be.repository.MovieCastRepository;
import com.be.repository.MovieRepository;
import com.be.repository.MovieTrailerRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TMDBService {

    @Value("${app.tmdb.base-url:''}")
    private String baseUrl;

    @Value("${app.tmdb.bearer-token:''}")
    private String bearerToken;

    private final String BASE_URL = "https://api.themoviedb.org/3" ;
    private final String BEARER_TOKEN = bearerToken;
    private final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";
    private static final int BATCH_SIZE = 20;
    private final RestTemplate restTemplate;
    private final MovieRepository movieRepository;
    private final MovieTrailerRepository movieTrailerRepository;
    private final MovieCastRepository movieCastRepository;
    private final CastRepository castRepository;

    // Image sizes available from TMDB
    public static class ImageSize {
        // Poster sizes
        public static final String POSTER_SMALL = "w185";
        public static final String POSTER_MEDIUM = "w342";
        public static final String POSTER_LARGE = "w500";
        public static final String POSTER_ORIGINAL = "original";

        // Backdrop sizes
        public static final String BACKDROP_SMALL = "w300";
        public static final String BACKDROP_MEDIUM = "w780";
        public static final String BACKDROP_LARGE = "w1280";
        public static final String BACKDROP_ORIGINAL = "original";
    }

    public String getFullPosterPath(String posterPath) {
        if (posterPath == null) return null;
        return BASE_IMAGE_URL + ImageSize.POSTER_ORIGINAL + posterPath;
    }

    public String getFullBackdropPath(String backdropPath) {
        if (backdropPath == null) return null;
        return BASE_IMAGE_URL + ImageSize.BACKDROP_ORIGINAL + backdropPath;
    }


    public TMDBService(RestTemplate restTemplate,
                       MovieRepository movieRepository,
                       MovieTrailerRepository movieTrailerRepository,
                       MovieCastRepository movieCastRepository,
                       CastRepository castRepository) {
        this.movieTrailerRepository = movieTrailerRepository;
        this.movieCastRepository = movieCastRepository;
        this.castRepository = castRepository;
        // Configure RestTemplate with headers
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("accept", "application/json");
            request.getHeaders().add("Authorization", "Bearer " + BEARER_TOKEN);
            return execution.execute(request, body);
        });
        this.restTemplate = restTemplate;
        this.movieRepository = movieRepository;
    }

    public Movie importMovieFromTMDB(Long tmdbId) {
        String url = String.format("%s/movie/%d", BASE_URL, tmdbId);
        TMDBMovieDTO tmdbMovie = restTemplate.getForObject(url, TMDBMovieDTO.class);

        return Movie.builder()
                .id(tmdbMovie.getId())
                .title(tmdbMovie.getTitle())
                .originalTitle(tmdbMovie.getOriginalTitle())
                .overview(tmdbMovie.getOverview())
                .releaseDate(LocalDate.parse(tmdbMovie.getReleaseDate()))
                .posterPath(tmdbMovie.getPosterPath())
                .backdropPath(tmdbMovie.getBackdropPath())
                .popularity(tmdbMovie.getPopularity())
                .voteAverage(tmdbMovie.getVoteAverage())
                .voteCount(tmdbMovie.getVoteCount())
                .build();
    }

    // Multi-search (movies, people, tv shows)
    public TMDBSearchResultDTO searchMulti(String query, int page) {
        String url = String.format("%s/search/multi?query=%s&page=%d",
                BASE_URL, query, page);
        return restTemplate.getForObject(url, TMDBSearchResultDTO.class);
    }

    // Movie-specific search
    public TMDBSearchResultDTO searchMovies(String query, int page) {
        String url = String.format("%s/search/movie?query=%s&include_adult=false&language=en-US&page=%d",
                BASE_URL, query, page);

        log.info("searchMovies - TMDB API Request: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("searchMovies - TMDB API Response: {}", response.getBody());

        return restTemplate.getForObject(url, TMDBSearchResultDTO.class);
    }

    // Get trending movies (day/week)
    public List<TMDBTrendingItemDTO> getTrendingMovies(String timeWindow) { // timeWindow: "day" or "week"
        String url = String.format("%s/trending/movie/%s",
                BASE_URL, timeWindow);
        return restTemplate.getForObject(url, TMDBTrendingResponse.class).getResults();
    }

    public TMDBTrendingResponse getTrending(String timeWindow) {
        String url = String.format("%s/trending/movie/%s?language=en-US",
                BASE_URL, timeWindow);

        log.info("getTrending - TMDB API Request - Trending: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("getTrending - TMDB API Response: {}", response.getBody());

        return restTemplate.getForObject(url, TMDBTrendingResponse.class);
    }

    // Get trending movies by today
    public List<TMDBTrendingItemDTO> getTrendingMoviesToday() {
        return getTrendingMovies("day");
    }

    // Get trending movies by week
    public List<TMDBTrendingItemDTO> getTrendingMoviesThisWeek() {
        return getTrendingMovies("week");
    }

    // Get movie trailers
    public List<TMDBVideoDTO> getMovieTrailers(Long movieId) {
        String url = String.format("%s/movie/%d/videos",
                BASE_URL, movieId);
        TMDBVideoResponse response = restTemplate.getForObject(url, TMDBVideoResponse.class);
        return response.getResults().stream()
                .filter(video -> "Trailer".equals(video.getType()))
                .collect(Collectors.toList());
    }

    // Get latest trailers (combine with upcoming movies)
    public List<TMDBMovieTrailerDTO> getLatestTrailers() {
        String upcomingUrl = String.format("%s/movie/upcoming",
                BASE_URL);
        TMDBMovieResponse upcoming = restTemplate.getForObject(upcomingUrl, TMDBMovieResponse.class);

        return upcoming.getResults().stream()
                .map(movie -> {
                    List<TMDBVideoDTO> trailers = getMovieTrailers(movie.getId());
                    return new TMDBMovieTrailerDTO(movie, trailers);
                })
                .filter(mt -> !mt.getTrailers().isEmpty())
                .collect(Collectors.toList());
    }

    // Get popular movies
    public TMDBMovieResponse getPopularMovies(int page) {
        String url = String.format("%s/movie/popular?language=en-US&page=%d", BASE_URL, page);

        log.info("TMDB API Request - Get Popular Movies: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("TMDB API Response - Get Popular Movies: {}", response.getBody());

        return restTemplate.getForObject(url, TMDBMovieResponse.class);
    }

    // Get top rated movies
    public List<TMDBMovieDTO> getTopRatedMovies(int page) {
        String url = String.format("%s/movie/top_rated?page=%d",
                BASE_URL, page);
        return restTemplate.getForObject(url, TMDBMovieResponse.class).getResults();
    }

    public TMDBMovieDTO getMovieDetails(Long movieId) {
        String url = String.format("%s/movie/%d?language=en-US", BASE_URL, movieId);

        log.info("getMovieDetails - TMDB API Request - Get Movie Details: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("getMovieDetails - TMDB API Response: {}", response.getBody());

        return restTemplate.getForObject(url, TMDBMovieDTO.class);
    }


    // Get Movie Reviews
    public TMDBReviewResponse getMovieReviews(Long movieId, int page) {
        String url = String.format("%s/movie/%d/reviews?language=en-US&page=%d",
                BASE_URL, movieId, page);

        log.info("TMDB API Request - Get Movie Reviews: {}", url);
        return restTemplate.getForObject(url, TMDBReviewResponse.class);
    }

    // Rate Movie
    public void rateMovie(Long movieId, double rating) {
        String url = String.format("%s/movie/%d/rating", BASE_URL, movieId);

        Map<String, Object> body = new HashMap<>();
        body.put("value", rating);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body);
        restTemplate.postForEntity(url, request, Void.class);
    }

    // Add to Favorites
    public void addToFavorites(Long movieId, String accountId, boolean favorite) {
        String url = String.format("%s/account/%s/favorite", BASE_URL, accountId);

        Map<String, Object> body = new HashMap<>();
        body.put("media_type", "movie");
        body.put("media_id", movieId);
        body.put("favorite", favorite);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body);
        restTemplate.postForEntity(url, request, Void.class);
    }

    // Add to Watchlist
    public void addToWatchlist(Long movieId, String accountId, boolean watchlist) {
        String url = String.format("%s/account/%s/watchlist", BASE_URL, accountId);

        Map<String, Object> body = new HashMap<>();
        body.put("media_type", "movie");
        body.put("media_id", movieId);
        body.put("watchlist", watchlist);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body);
        restTemplate.postForEntity(url, request, Void.class);
    }


    /*===================================================SYNC===================================================SYNC*/
    @Async
    public CompletableFuture<String> syncTrendingMovies() {
        try {
            log.info("Started syncing trending movies");
            try {
                log.info("Starting sync of trending movies day");
                TMDBTrendingResponse response = getTrending("day");

                for (TMDBTrendingItemDTO item : response.getResults()) {
                    saveOrUpdateMovieTrending(item, CategoryType.TRENDING_DAY);
                }
                log.info("Completed sync of {} trending movies day", response.getResults().size());
            } catch (Exception e) {
                log.error("Error syncing trending movies: ", e);
            }

            try {
                log.info("Starting sync of trending movies week");
                TMDBTrendingResponse response = getTrending("day");

                for (TMDBTrendingItemDTO item : response.getResults()) {
                    saveOrUpdateMovieTrending(item, CategoryType.TRENDING_WEEK);
                }
                log.info("Completed sync of {} trending movies week", response.getResults().size());
            } catch (Exception e) {
                log.error("Error syncing trending movies: ", e);
            }

            log.info("Completed syncing trending movies");
            return CompletableFuture.completedFuture("Trending movies sync completed successfully");
        } catch (Exception e) {
            log.error("Error syncing trending movies: ", e);
            return CompletableFuture.completedFuture("Error syncing trending movies: " + e.getMessage());
        }
    }

    @Async
    public CompletableFuture<String> syncPopularMovies() {
        try {
            log.info("Started syncing popular movies");
            TMDBMovieResponse response = getPopularMovies(1);  // Get first page

            for (TMDBMovieDTO movieDTO : response.getResults()) {
                Movie movie = movieRepository.findByTmdbId(movieDTO.getId())
                        .orElse(new Movie());

                updateMovieFromTMDB(movie, movieDTO);
                MovieCategory category;
                if (CollectionUtils.isEmpty(movie.getCategories())) {
                    category = new MovieCategory();
                } else {
                    category = movie.getCategories().stream()
                            .filter(mc -> Objects.equals(mc.getCategory(), CategoryType.POPULAR.name()))
                            .findFirst()
                            .orElse(new MovieCategory());
                }

                category.setMovie(movie);
                category.setCategory(CategoryType.POPULAR.name());
                category.setCreatedAt(ZonedDateTime.now());
                category.setUpdatedAt(ZonedDateTime.now());

                if (CollectionUtils.isEmpty(movie.getCategories())) {
                    movie.setCategories(new HashSet<>(List.of(category)));
                } else {
                    movie.getCategories().add(category);
                }

                movieRepository.save(movie);
            }

            log.info("Completed syncing popular movies");
            return CompletableFuture.completedFuture("Popular movies sync completed successfully");
        } catch (Exception e) {
            log.error("Error syncing popular movies: ", e);
            return CompletableFuture.completedFuture("Error syncing popular movies: " + e.getMessage());
        }
    }

    // Get movie videos (trailers)
    public TMDBVideoResponse getMovieVideos(Long movieId) {
        String url = String.format("%s/movie/%d/videos?language=en-US", BASE_URL, movieId);

        log.info("TMDB API Request - Get Movie Videos: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("TMDB API Response - Get Movie Videos: {}", response.getBody());

        return restTemplate.getForObject(url, TMDBVideoResponse.class);
    }

    // Get upcoming movies to find latest trailers
    public TMDBMovieResponse getUpcomingMovies() {
        String url = String.format("%s/movie/upcoming?language=en-US", BASE_URL);
        return restTemplate.getForObject(url, TMDBMovieResponse.class);
    }

    @Async
    @Transactional
    public CompletableFuture<String> syncLatestTrailers() {
        try {
            log.info("Started syncing latest trailers");
            TMDBMovieResponse upcomingMovies = getUpcomingMovies();

            for (TMDBMovieDTO movieDTO : upcomingMovies.getResults()) {
                // Get movie trailers
                TMDBVideoResponse videos = getMovieVideos(movieDTO.getId());

                // Filter for official trailers only
                List<TMDBVideoDTO> trailers = videos.getResults().stream()
                        .filter(v -> "Trailer".equals(v.getType()) && v.isOfficial())
                        .collect(Collectors.toList());

                if (!trailers.isEmpty()) {
                    // Save or update movie first
                    Movie movie = movieRepository.findByTmdbId(movieDTO.getId())
                            .orElse(new Movie());

                    updateMovieFromTMDB(movie, movieDTO);
                    movie = movieRepository.save(movie);  // Save movie first and get the managed entity

                    // Save trailers
                    for (TMDBVideoDTO trailer : trailers) {
                        MovieTrailer movieTrailer = MovieTrailer.builder()
                                .movie(movie)  // Use the managed movie entity
                                .key(trailer.getKey())
                                .name(trailer.getName())
                                .site(trailer.getSite())
                                .type(trailer.getType())
                                .official(trailer.isOfficial())
                                .publishedAt(Instant.parse(trailer.getPublishedAt())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime())
                                .build();

                        movieTrailerRepository.save(movieTrailer);
                    }
                }
            }

            log.info("Completed syncing latest trailers");
            return CompletableFuture.completedFuture("Latest trailers sync completed successfully");
        } catch (Exception e) {
            log.error("Error syncing latest trailers: ", e);
            return CompletableFuture.completedFuture("Error syncing latest trailers: " + e.getMessage());
        }
    }

    private void saveOrUpdateMovieTrending(TMDBTrendingItemDTO item, CategoryType categoryType) {
        try {
            // Get full movie details from TMDB
            TMDBMovieDTO movieDetails = getMovieDetails(item.getId());

            // Find existing movie or create new one
            Movie movie = movieRepository.findByTmdbId(item.getId())
                    .orElse(new Movie());

            // Update movie details
            movie.setTmdbId(item.getId());
            movie.setTitle(item.getTitle());
            movie.setOriginalTitle(item.getOriginalTitle());
            movie.setOverview(item.getOverview());
            movie.setPopularity(item.getPopularity());
            // TODO adult, video, media_type, profile_path, first_air_date

            movie.setPosterPath(item.getPosterPath());
            movie.setBackdropPath(item.getBackdropPath());

            movie.setPosterUrl(getFullPosterPath(item.getPosterPath()));
            movie.setBackdropUrl(getFullBackdropPath(item.getBackdropPath()));

            movie.setVoteAverage(item.getVoteAverage());
            movie.setVoteCount(item.getVoteCount());

            if (item.getReleaseDate() != null) {
                movie.setReleaseDate(LocalDate.parse(item.getReleaseDate()));
            }

            MovieCategory category;
            if (CollectionUtils.isEmpty(movie.getCategories())) {
                category = new MovieCategory();
            } else {
                category = movie.getCategories().stream()
                        .filter(mc -> Objects.equals(mc.getCategory(), categoryType.name()))
                        .findFirst()
                        .orElse(new MovieCategory());
            }

            category.setMovie(movie);
            category.setCategory(categoryType.name());
            category.setCreatedAt(ZonedDateTime.now());
            category.setUpdatedAt(ZonedDateTime.now());

            if (CollectionUtils.isEmpty(movie.getCategories())) {
                movie.setCategories(new HashSet<>(List.of(category)));
            } else {
                movie.getCategories().add(category);
            }

            // Save to database
            movieRepository.save(movie);
            log.info("Saved/Updated movie: {}", movie.getTitle());
        } catch (Exception e) {
            log.error("Error saving movie {}: ", item.getTitle(), e);
        }
    }

    /*SYNC SCHEDULED*/
    @Scheduled(cron = "0 0 */4 * * *") // Every 4 hours
    public void scheduledTrendingSync() {
        syncTrendingMovies();
    }

    @Scheduled(cron = "0 0 1 * * *") // Once a day at 1 AM
    public void scheduledPopularSync() {
        syncPopularMovies();
    }

    private void updateMovieFromTMDB(Movie movie, TMDBMovieDTO tmdbMovie) {
        movie.setTmdbId(tmdbMovie.getId());
        movie.setTitle(tmdbMovie.getTitle());
        movie.setOriginalTitle(tmdbMovie.getOriginalTitle());
        movie.setOverview(tmdbMovie.getOverview());
        movie.setReleaseDate(LocalDate.parse(tmdbMovie.getReleaseDate()));
        movie.setPosterPath(tmdbMovie.getPosterPath());
        movie.setBackdropPath(tmdbMovie.getBackdropPath());
        movie.setPopularity(tmdbMovie.getPopularity());
        movie.setVoteAverage(tmdbMovie.getVoteAverage());
        movie.setVoteCount(tmdbMovie.getVoteCount());
        movie.setPosterUrl(getFullPosterPath(tmdbMovie.getPosterPath()));
        movie.setBackdropUrl(getFullBackdropPath(tmdbMovie.getBackdropPath()));
    }

    // Get Movie Credits (Cast & Crew)
    public TMDBCreditsResponse getMovieCredits(Long movieId) {
        String url = String.format("%s/movie/%d/credits?language=en-US", BASE_URL, movieId);

        log.info("TMDB API Request - Get Movie Credits: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("TMDB API Response: {}", response.getBody());

        return restTemplate.getForObject(url, TMDBCreditsResponse.class);
    }

    /*Sync movie cast*/
    @Async
    @Transactional
    public CompletableFuture<String> syncMovieCasts(Long movieId) {
        try {
            log.info("Started syncing cast for movie ID: {}", movieId);

            Movie movie = movieRepository.findByTmdbId(movieId)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));

            TMDBCreditsResponse credits = getMovieCredits(movieId);

            for (TMDBCastDTO castDTO : credits.getCast()) {
                // Find or create cast
                Cast cast = castRepository.findByTmdbId(castDTO.getId())
                        .orElseGet(() -> {
                            Cast newCast = Cast.builder()
                                    .tmdbId(castDTO.getId())
                                    .name(castDTO.getName())
                                    .profilePath(castDTO.getProfile_path())
                                    .build();
                            return castRepository.save(newCast);
                        });

                // Create movie cast relationship if it doesn't exist
                MovieCastId movieCastId = new MovieCastId(movie.getId(), cast.getId(), castDTO.getCharacter());

                if (!movieCastRepository.existsById(movieCastId)) {
                    MovieCast movieCast = MovieCast.builder()
                            .id(movieCastId)
                            .movie(movie)
                            .cast(cast)
                            .role(castDTO.getKnown_for_department())
                            .build();

                    movieCastRepository.save(movieCast);
                }
            }

            log.info("Completed syncing cast for movie ID: {}", movieId);
            return CompletableFuture.completedFuture("Cast sync completed successfully");
        } catch (Exception e) {
            log.error("Error syncing cast for movie ID {}: ", movieId, e);
            return CompletableFuture.completedFuture("Error syncing cast: " + e.getMessage());
        }
    }

    @Transactional
    public void syncMovieCast(Movie movie) {
        log.info("Syncing cast for movie: {} (ID: {})", movie.getTitle(), movie.getId());

        if (movie.getTmdbId() == null) {
            log.warn("Skipping movie {} - No TMDB ID", movie.getTitle());
            return;
        }

        TMDBCreditsResponse credits = getMovieCredits(movie.getTmdbId());

        for (TMDBCastDTO castDTO : credits.getCast()) {
            try {
                // Find or create cast
                Cast cast = castRepository.findByTmdbId(castDTO.getId())
                        .orElseGet(() -> {
                            Cast newCast = Cast.builder()
                                    .tmdbId(castDTO.getId())
                                    .name(castDTO.getName())
                                    .profilePath(castDTO.getProfile_path())
                                    .build();
                            return castRepository.save(newCast);
                        });

                // Create movie cast relationship
                MovieCastId movieCastId = new MovieCastId(movie.getId(), cast.getId(), castDTO.getCharacter());

                if (!movieCastRepository.existsById(movieCastId)) {
                    MovieCast movieCast = MovieCast.builder()
                            .id(movieCastId)
                            .movie(movie)
                            .cast(cast)
                            .role(castDTO.getKnown_for_department())
                            .build();

                    movieCastRepository.save(movieCast);
                }
            } catch (Exception e) {
                log.error("Error processing cast member {} for movie {}: ",
                        castDTO.getName(), movie.getTitle(), e);
            }
        }
    }

    @Async
    @Transactional
    public CompletableFuture<String> syncAllMovieCasts() {
        try {
            log.info("Started batch sync of movie casts");

            // Get all movies that need cast sync
            List<Movie> movies = movieRepository.findAll();
            int totalMovies = movies.size();
            int processedMovies = 0;
            List<String> errors = new ArrayList<>();

            // Process movies in batches
            for (int i = 0; i < movies.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, movies.size());
                List<Movie> batch = movies.subList(i, endIndex);

                // Process each movie in the batch
                for (Movie movie : batch) {
                    try {
                        syncMovieCast(movie);
                        processedMovies++;
                        log.info("Progress: {}/{} movies processed", processedMovies, totalMovies);
                    } catch (Exception e) {
                        String error = String.format("Error syncing cast for movie %s (ID: %d): %s",
                                movie.getTitle(), movie.getId(), e.getMessage());
                        errors.add(error);
                        log.error(error, e);
                    }
                }

                // Add delay between batches to avoid rate limiting
                Thread.sleep(1000); // 1 second delay
            }

            // Prepare completion message
            String result = String.format("Completed syncing casts for %d movies. ", processedMovies);
            if (!errors.isEmpty()) {
                result += String.format("Errors occurred for %d movies.", errors.size());
            }

            log.info(result);
            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            log.error("Error in batch sync process: ", e);
            return CompletableFuture.completedFuture("Error in batch sync: " + e.getMessage());
        }
    }

}