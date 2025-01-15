package com.be.service.external;

import com.be.appexception.ResourceNotFoundException;
import com.be.model.dto.tmdb.*;
import com.be.model.entity.*;
import com.be.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TMDBService {

    @Value("${app.tmdb.token:''}")
    private String bearerToken;

    private final String BASE_URL = "https://api.themoviedb.org/3";
    private final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";
    private static final int BATCH_SIZE = 20;
    private final RestTemplate restTemplate;
    private final MovieRepository movieRepository;
    private final MovieTrailerRepository movieTrailerRepository;
    private final MovieCastRepository movieCastRepository;
    private final CastRepository castRepository;
    private final GenreRepository genreRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TransactionTemplate transactionTemplate;
    private final SystemUserService systemUserService;

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
                       CastRepository castRepository,
                       GenreRepository genreRepository,
                       ReviewRepository reviewRepository,
                       UserRepository userRepository,
                       TransactionTemplate transactionTemplate, SystemUserService systemUserService) {
        this.movieTrailerRepository = movieTrailerRepository;
        this.movieCastRepository = movieCastRepository;
        this.castRepository = castRepository;
        this.genreRepository = genreRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.transactionTemplate = transactionTemplate;
        this.systemUserService = systemUserService;
        // Configure RestTemplate with headers
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("accept", "application/json");
            request.getHeaders().add("Authorization", "Bearer " + bearerToken);
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

        log.info("TMDB API Request - Get Movie Details: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("TMDB API Response: {}", response.getBody());

        return restTemplate.getForObject(url, TMDBMovieDTO.class);
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
                try {
                    // Get full movie details
                    TMDBMovieDTO fullMovieDetails = getMovieDetails(movieDTO.getId());

                    Movie movie = movieRepository.findByTmdbId(movieDTO.getId())
                            .orElse(new Movie());

                    updateMovieFromTMDB(movie, fullMovieDetails);

                    // Update category
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
                    log.info("Saved/Updated popular movie: {}", movie.getTitle());
                } catch (Exception e) {
                    log.error("Error processing popular movie {}: ", movieDTO.getTitle(), e);
                }
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
    public CompletableFuture<String> syncLatestTrailers() {
        try {
            log.info("Started syncing latest trailers");

            // Get upcoming movies first
            TMDBMovieResponse upcomingMovies = getUpcomingMovies();
            int totalMovies = upcomingMovies.getResults().size();
            int processedMovies = 0;
            int totalTrailers = 0;

            for (TMDBMovieDTO movieDTO : upcomingMovies.getResults()) {
                try {
                    // Get movie's videos/trailers
                    TMDBVideoResponse videos = getMovieVideos(movieDTO.getId());

                    // Find or create movie
                    Movie movie = movieRepository.findByTmdbId(movieDTO.getId())
                            .orElseGet(() -> {
                                Movie newMovie = new Movie();
                                updateMovieFromTMDB(newMovie, movieDTO);
                                return movieRepository.save(newMovie);
                            });

                    // Process each video
                    for (TMDBVideoDTO video : videos.getResults()) {
                        // Only process trailers
                        if ("Trailer".equals(video.getType())) {
                            MovieTrailer trailer = movieTrailerRepository
                                    .findByTmdbId(Long.valueOf(video.getId()))
                                    .orElse(new MovieTrailer());

                            trailer.setMovie(movie);
                            trailer.setTmdbId(Long.valueOf(video.getId()));
                            trailer.setName(video.getName());
                            trailer.setKey(video.getKey());
                            trailer.setSite(video.getSite());
                            trailer.setType(video.getType());
                            trailer.setOfficial(video.getOfficial());
                            if (video.getPublished_at() != null) {
                                trailer.setPublishedAt(ZonedDateTime.parse(video.getPublished_at()).toLocalDateTime());
                            }

                            movieTrailerRepository.save(trailer);
                            totalTrailers++;
                        }
                    }

                    processedMovies++;
                    log.info("Progress: {}/{} movies processed, {} trailers found",
                            processedMovies, totalMovies, totalTrailers);

                } catch (Exception e) {
                    log.error("Error processing trailers for movie {}: {}",
                            movieDTO.getTitle(), e.getMessage());
                }
            }

            String result = String.format(
                    "Completed syncing %d trailers from %d upcoming movies",
                    totalTrailers, processedMovies
            );
            log.info(result);
            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            log.error("Error syncing latest trailers: ", e);
            return CompletableFuture.completedFuture(
                    "Error syncing latest trailers: " + e.getMessage()
            );
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

            // genre
            Set<Genre> genres = new HashSet<>();
            if (item.getGenreIds() != null) {
                for (Integer genreId : item.getGenreIds()) {
                    Genre genre = genreRepository.findByTmdbId(Long.valueOf(genreId))
                            .orElseGet(() -> {
                                // If genre doesn't exist, create it
                                TMDBGenreDTO genreDetails = getGenreDetails(Long.valueOf(genreId));
                                if (genreDetails != null) {
                                    Genre newGenre = Genre.builder()
                                            .tmdbId(genreDetails.getId())
                                            .name(genreDetails.getName())
                                            .build();
                                    return genreRepository.save(newGenre);
                                }
                                return null;
                            });

                    if (genre != null) {
                        genres.add(genre);
                    }
                }
            }
            movie.setGenres(genres);

            // New fields
            movie.setAdult(movieDetails.getAdult());

            if (movieDetails.getBelongs_to_collection() != null) {
                MovieCollection collection = new MovieCollection();
                collection.setId(movieDetails.getBelongs_to_collection().getId());
                collection.setName(movieDetails.getBelongs_to_collection().getName());
                collection.setPosterPath(movieDetails.getBelongs_to_collection().getPoster_path());
                collection.setBackdropPath(movieDetails.getBelongs_to_collection().getBackdrop_path());
                movie.setCollection(collection);
            }

            movie.setBudget(movieDetails.getBudget());
            movie.setHomepage(movieDetails.getHomepage());
            movie.setImdbId(movieDetails.getImdbId());
            movie.setOriginalLanguage(movieDetails.getOriginalLanguage());
            movie.setRevenue(movieDetails.getRevenue());
            movie.setStatus(movieDetails.getStatus());
            movie.setTagline(movieDetails.getTagline());

            if (movieDetails.getReleaseDate() != null) {
                movie.setReleaseDate(LocalDate.parse(movieDetails.getReleaseDate()));
            }

            // Origin countries
            if (movieDetails.getOriginCountry() != null) {
                movie.setOriginCountries(new HashSet<>(movieDetails.getOriginCountry()));
            }

            // Production companies
            if (movieDetails.getProduction_companies() != null) {
                Set<MovieProductionCompany> companies = movieDetails.getProduction_companies().stream()
                        .map(companyDTO -> {
                            MovieProductionCompany company = new MovieProductionCompany();
                            company.setTmdbId(companyDTO.getId());
                            company.setName(companyDTO.getName());
                            company.setLogoPath(companyDTO.getLogo_path());
                            company.setOriginCountry(companyDTO.getOrigin_country());
                            company.setMovie(movie);
                            return company;
                        })
                        .collect(Collectors.toSet());
                movie.setProductionCompanies(companies);
            }

            // Production countries
            if (movieDetails.getProduction_countries() != null) {
                Set<ProductionCountry> countries = movieDetails.getProduction_countries().stream()
                        .map(countryDTO -> {
                            ProductionCountry country = new ProductionCountry();
                            country.setIso31661(countryDTO.getIso_3166_1());
                            country.setName(countryDTO.getName());
                            return country;
                        })
                        .collect(Collectors.toSet());
                movie.setProductionCountries(countries);
            }

            // Spoken languages
            if (movieDetails.getSpoken_languages() != null) {
                Set<SpokenLanguage> languages = movieDetails.getSpoken_languages().stream()
                        .map(langDTO -> {
                            SpokenLanguage language = new SpokenLanguage();
                            language.setEnglishName(langDTO.getEnglish_name());
                            language.setIso6391(langDTO.getIso_639_1());
                            language.setName(langDTO.getName());
                            return language;
                        })
                        .collect(Collectors.toSet());
                movie.setSpokenLanguages(languages);
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
        // Basic fields
        movie.setTmdbId(tmdbMovie.getId());
        movie.setTitle(tmdbMovie.getTitle());
        movie.setOriginalTitle(tmdbMovie.getOriginalTitle());
        movie.setOverview(tmdbMovie.getOverview());
        if (tmdbMovie.getReleaseDate() != null) {
            movie.setReleaseDate(LocalDate.parse(tmdbMovie.getReleaseDate()));
        }
        movie.setPosterPath(tmdbMovie.getPosterPath());
        movie.setBackdropPath(tmdbMovie.getBackdropPath());
        movie.setPopularity(tmdbMovie.getPopularity());
        movie.setVoteAverage(tmdbMovie.getVoteAverage());
        movie.setVoteCount(tmdbMovie.getVoteCount());
        movie.setPosterUrl(getFullPosterPath(tmdbMovie.getPosterPath()));
        movie.setBackdropUrl(getFullBackdropPath(tmdbMovie.getBackdropPath()));

        // New fields
        movie.setAdult(tmdbMovie.getAdult());
        if (tmdbMovie.getBelongs_to_collection() != null) {
            MovieCollection collection = new MovieCollection();
            collection.setId(tmdbMovie.getBelongs_to_collection().getId());
            collection.setName(tmdbMovie.getBelongs_to_collection().getName());
            collection.setPosterPath(tmdbMovie.getBelongs_to_collection().getPoster_path());
            collection.setBackdropPath(tmdbMovie.getBelongs_to_collection().getBackdrop_path());
            movie.setCollection(collection);
        }

        movie.setBudget(tmdbMovie.getBudget());
        movie.setHomepage(tmdbMovie.getHomepage());
        movie.setImdbId(tmdbMovie.getImdbId());
        movie.setOriginalLanguage(tmdbMovie.getOriginalLanguage());
        movie.setRevenue(tmdbMovie.getRevenue());
        movie.setRuntime(Float.valueOf(tmdbMovie.getRuntime()));
        movie.setStatus(tmdbMovie.getStatus());
        movie.setTagline(tmdbMovie.getTagline());

        // Set production companies
        if (tmdbMovie.getProduction_companies() != null) {
            Set<MovieProductionCompany> companies = tmdbMovie.getProduction_companies().stream()
                    .map(companyDTO -> {
                        MovieProductionCompany company = new MovieProductionCompany();
                        company.setTmdbId(companyDTO.getId());
                        company.setName(companyDTO.getName());
                        company.setLogoPath(companyDTO.getLogo_path());
                        company.setOriginCountry(companyDTO.getOrigin_country());
                        company.setMovie(movie);
                        return company;
                    })
                    .collect(Collectors.toSet());
            movie.setProductionCompanies(companies);
        }

        // Set production countries
        if (tmdbMovie.getProduction_countries() != null) {
            Set<ProductionCountry> countries = tmdbMovie.getProduction_countries().stream()
                    .map(countryDTO -> {
                        ProductionCountry country = new ProductionCountry();
                        country.setIso31661(countryDTO.getIso_3166_1());
                        country.setName(countryDTO.getName());
                        return country;
                    })
                    .collect(Collectors.toSet());
            movie.setProductionCountries(countries);
        }

        // Set spoken languages
        if (tmdbMovie.getSpoken_languages() != null) {
            Set<SpokenLanguage> languages = tmdbMovie.getSpoken_languages().stream()
                    .map(langDTO -> {
                        SpokenLanguage language = new SpokenLanguage();
                        language.setEnglishName(langDTO.getEnglish_name());
                        language.setIso6391(langDTO.getIso_639_1());
                        language.setName(langDTO.getName());
                        return language;
                    })
                    .collect(Collectors.toSet());
            movie.setSpokenLanguages(languages);
        }
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

    // Get all genres from TMDB
    public TMDBGenreResponse getGenres() {
        String url = String.format("%s/genre/movie/list?language=en-US", BASE_URL);

        log.info("TMDB API Request - Get Genres: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("TMDB API Response: {}", response.getBody());

        return restTemplate.getForObject(url, TMDBGenreResponse.class);
    }

    @Async
    @Transactional
    public CompletableFuture<String> syncGenres() {
        try {
            log.info("Started syncing genres");
            TMDBGenreResponse genreResponse = getGenres();
            int count = 0;

            for (TMDBGenreDTO genreDTO : genreResponse.getGenres()) {
                try {
                    // Find or create genre
                    Genre genre = genreRepository.findByTmdbId(genreDTO.getId())
                            .orElse(new Genre());

                    genre.setTmdbId(genreDTO.getId());
                    genre.setName(genreDTO.getName());

                    genreRepository.save(genre);
                    count++;

                } catch (Exception e) {
                    log.error("Error syncing genre {}: ", genreDTO.getName(), e);
                }
            }

            String message = String.format("Successfully synced %d genres", count);
            log.info(message);
            return CompletableFuture.completedFuture(message);

        } catch (Exception e) {
            log.error("Error syncing genres: ", e);
            return CompletableFuture.completedFuture("Error syncing genres: " + e.getMessage());
        }
    }

    private TMDBGenreDTO getGenreDetails(Long genreId) {
        try {
            String url = String.format("%s/genre/%d?language=en-US", BASE_URL, genreId);
            return restTemplate.getForObject(url, TMDBGenreDTO.class);
        } catch (Exception e) {
            log.error("Error getting genre details for ID {}: ", genreId, e);
            return null;
        }
    }

    // Review
    public TMDBReviewResponse getMovieReviews(Long movieId, int page) {
        String url = String.format("%s/movie/%d/reviews?language=en-US&page=%d",
                BASE_URL, movieId, page);

        log.info("TMDB API Request - Get Movie Reviews: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("TMDB API Response: {}", response.getBody());

        return restTemplate.getForObject(url, TMDBReviewResponse.class);
    }

    @Async
    @Transactional
    public CompletableFuture<String> syncMovieReviews(Long movieId) {
        try {
            log.info("Started syncing reviews for movie ID: {}", movieId);

            Movie movie = movieRepository.findByTmdbId(movieId)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found", "", "", ""));

            int page = 1;
            int totalPages;
            int processedReviews = 0;

            do {
                TMDBReviewResponse reviewResponse = getMovieReviews(movieId, page);
                totalPages = reviewResponse.getTotalPages();

                for (TMDBReviewDTO reviewDTO : reviewResponse.getResults()) {
                    try {
                        // Find or create review
                        Review review = reviewRepository
                                .findByTmdbId(reviewDTO.getId())
                                .orElse(new Review());

                        // Create system user for TMDB reviews if not exists
                        User systemUser = userRepository
                                .findByEmail("tmdb_" + reviewDTO.getAuthor() + "@system.local")
                                .orElseGet(() -> {
                                    User newUser = User.builder()
                                            .email("tmdb_" + reviewDTO.getAuthor() + "@system.local")
                                            .fullName(reviewDTO.getAuthor())
                                            .provider("TMDB")
                                            .build();
                                    return userRepository.save(newUser);
                                });

                        // Update review
                        review.setTmdbId(reviewDTO.getId());
                        review.setMovie(movie);
                        review.setUser(systemUser);
                        review.setContent(reviewDTO.getContent());
                        review.setRating(reviewDTO.getAuthorDetails().getRating());
                        review.setCreatedAt(ZonedDateTime.parse(reviewDTO.getCreatedAt()));

                        reviewRepository.save(review);
                        processedReviews++;

                    } catch (Exception e) {
                        log.error("Error processing review from {}: ",
                                reviewDTO.getAuthor(), e);
                    }
                }

                page++;
            } while (page <= totalPages);

            String message = String.format("Successfully synced %d reviews for movie %s",
                    processedReviews, movie.getTitle());
            log.info(message);
            return CompletableFuture.completedFuture(message);

        } catch (Exception e) {
            log.error("Error syncing reviews for movie ID {}: ", movieId, e);
            return CompletableFuture.completedFuture(
                    "Error syncing reviews: " + e.getMessage());
        }
    }


    @Async
    public CompletableFuture<String> syncAllMovieReviews() {
        try {
            log.info("Started syncing reviews for all movies");

            List<Movie> movies = movieRepository.findAll();
            int totalMovies = movies.size();
            int processedMovies = 0;
            int totalReviews = 0;
            List<String> errors = new ArrayList<>();

            // Process movies in batches
            for (int i = 0; i < movies.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, movies.size());
                List<Movie> batch = movies.subList(i, endIndex);

                for (Movie movie : batch) {
                    try {
                        if (movie.getTmdbId() != null) {
                            int reviewCount = syncReviewsForMovie(movie);
                            totalReviews += reviewCount;
                            processedMovies++;
                            log.info("Progress: {}/{} movies processed, {} reviews synced",
                                    processedMovies, totalMovies, totalReviews);
                        }
                    } catch (Exception e) {
                        String error = String.format("Error syncing reviews for movie %s (ID: %d): %s",
                                movie.getTitle(), movie.getId(), e.getMessage());
                        errors.add(error);
                        log.error(error, e);
                    }
                }

                // Add delay between batches to avoid rate limiting
                Thread.sleep(1000); // 1 second delay
            }

            String result = String.format(
                    "Completed syncing %d reviews across %d/%d movies. ",
                    totalReviews, processedMovies, totalMovies
            );
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

    private int syncReviewsForMovie(Movie movie) {
        int processedReviews = 0;
        int page = 1;
        int totalPages;

        do {
            TMDBReviewResponse reviewResponse = getMovieReviews(movie.getTmdbId(), page);
            totalPages = reviewResponse.getTotalPages();

            // Process each review
            for (TMDBReviewDTO reviewDTO : reviewResponse.getResults()) {
                try {
                    String username = reviewDTO.getAuthorDetails().getUsername();

                    // Create or get system user
                    User systemUser = systemUserService.getOrCreateSystemUser(username, reviewDTO.getAuthor());

                    // Create review in separate transaction
                    transactionTemplate.execute(status -> {
                        try {
                            Review review = reviewRepository.findByTmdbId(reviewDTO.getId())
                                    .orElse(new Review());

                            review.setTmdbId(reviewDTO.getId());
                            review.setMovie(movie);
                            review.setUser(systemUser);
                            review.setContent(reviewDTO.getContent());
                            review.setRating(reviewDTO.getAuthorDetails().getRating());

                            if (reviewDTO.getCreatedAt() != null) {
                                review.setCreatedAt(ZonedDateTime.parse(reviewDTO.getCreatedAt()));
                            }

                            reviewRepository.save(review);
                            return null;
                        } catch (Exception e) {
                            status.setRollbackOnly();
                            throw e;
                        }
                    });

                    processedReviews++;
                } catch (Exception e) {
                    log.error("Error processing review for movie {}: {}",
                            movie.getTitle(), e.getMessage());
                }
            }

            page++;
        } while (page <= totalPages);

        return processedReviews;
    }

    public TMDBPersonDTO getCastDetails(Long castId) {
        String url = String.format("%s/person/%d?append_to_response=movie_credits&language=en-US",
                BASE_URL, castId);

        log.info("TMDB API Request - Get Cast Details: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("TMDB API Response: {}", response.getBody());

        return restTemplate.getForObject(url, TMDBPersonDTO.class);
    }

    @Transactional
    public void syncCastDetails(Long castId) {
        try {
            TMDBPersonDTO personDTO = getCastDetails(castId);

            Cast cast = castRepository.findByTmdbId(personDTO.getId())
                    .orElse(new Cast());

            // Update cast details
            cast.setTmdbId(personDTO.getId());
            cast.setName(personDTO.getName());
            cast.setProfilePath(personDTO.getProfilePath());
            cast.setBiography(personDTO.getBiography());
            if (personDTO.getBirthDate() != null) {
                cast.setBirthDate(LocalDate.parse(personDTO.getBirthDate()));
            }
            cast.setPlaceOfBirth(personDTO.getPlaceOfBirth());
            cast.setKnownForDepartment(personDTO.getKnownForDepartment());
            cast.setPopularity(personDTO.getPopularity());
            cast.setGender(personDTO.getGender().toString());
            cast.setImdbId(personDTO.getImdbId());

            cast = castRepository.save(cast);

            // Sync acting credits
            if (personDTO.getMovieCredits() != null &&
                    personDTO.getMovieCredits().getCast() != null) {
                for (TMDBPersonCastDTO creditDTO : personDTO.getMovieCredits().getCast()) {
                    Movie movie = movieRepository.findByTmdbId(creditDTO.getId())
                            .orElseGet(() -> {
                                // Create basic movie if not exists
                                Movie newMovie = new Movie();
                                newMovie.setTmdbId(creditDTO.getId());
                                newMovie.setTitle(creditDTO.getTitle());
                                newMovie.setPosterPath(creditDTO.getPosterPath());
                                if (creditDTO.getReleaseDate() != null) {
                                    newMovie.setReleaseDate(LocalDate.parse(creditDTO.getReleaseDate()));
                                }
                                return movieRepository.save(newMovie);
                            });

                    MovieCastId movieCastId = new MovieCastId(movie.getId(), cast.getId(), creditDTO.getCharacter());

                    if (!movieCastRepository.existsById(movieCastId)) {
                        MovieCast movieCast = MovieCast.builder()
                                .id(movieCastId)
                                .movie(movie)
                                .cast(cast)
                                .role(personDTO.getKnownForDepartment())
                                .build();

                        movieCastRepository.save(movieCast);
                    }
                }
            }

            log.info("Successfully synced cast details for: {}", cast.getName());
        } catch (Exception e) {
            log.error("Error syncing cast details for ID {}: ", castId, e);
            throw e;
        }
    }
}