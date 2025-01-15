package com.be.service.impl;

import com.be.model.base.PageResponse;
import com.be.model.searchdto.KnownForMovie;
import com.be.model.searchdto.MultiSearchResponse;
import com.be.model.searchdto.SearchMovieResponse;
import com.be.model.searchdto.SearchPersonResponse;
import com.be.service.SearchService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Proxy;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public SearchServiceImpl(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    private static class SearchResult<T> {
        final List<T> content;
        final long totalElements;
        final int totalPages;

        SearchResult(List<T> content, long totalElements, int totalPages) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
    }

    private <T> SearchResult<T> executeSearch(String sql, Object[] params, RowMapper<T> rowMapper) {
        class ResultExtractor implements ResultSetExtractor<SearchResult<T>> {
            @Override
            public SearchResult<T> extractData(ResultSet rs) throws SQLException {
                List<T> results = new ArrayList<>();
                long totalElements = 0;
                int totalPages = 0;

                boolean first = true;
                while (rs.next()) {
                    if (first) {
                        totalElements = rs.getLong("total_results");
                        totalPages = rs.getInt("total_pages");
                        first = false;
                    }
                    results.add(rowMapper.mapRow(rs, results.size()));
                }

                return new SearchResult<>(results, totalElements, totalPages);
            }
        }

        return jdbcTemplate.query(sql, params, new ResultExtractor());
    }

    private <T> PageResponse<T> createPageResponse(SearchResult<T> searchResult, int page, int size) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(searchResult.content);
        response.setCurrentPage(page);
        response.setPageSize(size);
        response.setTotalElements(searchResult.totalElements);
        response.setTotalPages(searchResult.totalPages);
        response.setFirst(page == 0);
        response.setLast(page >= searchResult.totalPages - 1);
        response.setHasNext(page < searchResult.totalPages - 1);
        response.setHasPrevious(page > 0);
        return response;
    }

    public PageResponse<SearchMovieResponse> searchMovies(
            String query,
            int page,
            int size,
            Integer year,
            List<Integer> genreIds,
            boolean includeAdult
    ) {
        String sql = "SELECT * FROM search_movies(?, ?, ?, ?, ?, ?)";

        Object[] params = null;
        try {
            params = new Object[]{
                    query,
                    page + 1, // Convert to 1-based pagination for stored procedure
                    size,
                    year,
                    genreIds != null && !genreIds.isEmpty() ?
                            Objects.requireNonNull(jdbcTemplate.getDataSource())
                                    .getConnection()
                                    .createArrayOf("integer", genreIds.toArray()) :
                            null,
                    includeAdult
            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        RowMapper<SearchMovieResponse> rowMapper = (rs, rowNum) -> {
            SearchMovieResponse response = new SearchMovieResponse();
            response.setId(rs.getLong("id"));
            response.setTitle(rs.getString("title"));
            response.setOriginalTitle(rs.getString("original_title"));
            response.setOverview(rs.getString("overview"));
            response.setPosterPath(rs.getString("poster_path"));
            response.setPosterUrl(rs.getString("poster_url"));
            response.setBackdropPath(rs.getString("backdrop_path"));
            response.setReleaseDate(rs.getDate("release_date") != null ?
                    rs.getDate("release_date").toLocalDate() : null);
            response.setVoteAverage(rs.getFloat("vote_average"));
            response.setVoteCount(rs.getInt("vote_count"));
            response.setPopularity(rs.getFloat("popularity"));
            Array genreNames = rs.getArray("genre_names");
            if (genreNames != null) {
                response.setGenreNames(List.of((String[]) genreNames.getArray()));
            }
            return response;
        };

        SearchResult<SearchMovieResponse> searchResult = executeSearch(sql, params, rowMapper);
        return createPageResponse(searchResult, page, size);
    }

    public PageResponse<SearchPersonResponse> searchPeople(
            String query,
            int page,
            int size
    ) {
        String sql = "SELECT * FROM search_people(?, ?, ?)";

        Object[] params = new Object[]{query, page + 1, size};

        RowMapper<SearchPersonResponse> rowMapper = (rs, rowNum) -> {
            SearchPersonResponse response = new SearchPersonResponse();
            response.setId(rs.getLong("id"));
            response.setName(rs.getString("name"));
            response.setProfilePath(rs.getString("profile_path"));
            response.setPopularity(rs.getFloat("popularity"));
            response.setKnownForDepartment(rs.getString("known_for_department"));

            String knownForJson = rs.getString("known_for");
            if (knownForJson != null) {
                try {
                    response.setKnownFor(
                            objectMapper.readValue(knownForJson,
                                    new TypeReference<List<KnownForMovie>>() {
                                    })
                    );
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing known_for JSON", e);
                }
            }

            return response;
        };

        SearchResult<SearchPersonResponse> searchResult = executeSearch(sql, params, rowMapper);
        return createPageResponse(searchResult, page, size);
    }

    public PageResponse<MultiSearchResponse> multiSearch(
            String query,
            int page,
            int size,
            boolean includeAdult
    ) {
        String sql = "SELECT * FROM multi_search(?, ?, ?, ?)";

        Object[] params = new Object[]{query, page + 1, size, includeAdult};

        RowMapper<MultiSearchResponse> rowMapper = (rs, rowNum) -> {
            MultiSearchResponse response = new MultiSearchResponse();
            response.setId(rs.getLong("id"));
            response.setMediaType(rs.getString("media_type"));
            response.setTitle(rs.getString("title"));
            response.setName(rs.getString("name"));
            response.setOverview(rs.getString("overview"));
            response.setProfilePath(rs.getString("profile_path"));
            response.setPosterPath(rs.getString("poster_path"));
            response.setBackdropPath(rs.getString("backdrop_path"));
            response.setPopularity(rs.getFloat("popularity"));
            response.setReleaseDate(rs.getDate("release_date") != null ?
                    rs.getDate("release_date").toLocalDate() : null);
            response.setVoteAverage(rs.getFloat("vote_average"));
            return response;
        };

        SearchResult<MultiSearchResponse> searchResult = executeSearch(sql, params, rowMapper);
        return createPageResponse(searchResult, page, size);
    }
}
