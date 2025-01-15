package com.be.service;

import com.be.model.base.PageResponse;
import com.be.model.searchdto.MultiSearchResponse;
import com.be.model.searchdto.SearchMovieResponse;
import com.be.model.searchdto.SearchPersonResponse;

import java.sql.SQLException;
import java.util.List;

public interface SearchService {
    PageResponse<SearchMovieResponse> searchMovies(
            String query,
            int page,
            int size,
            Integer year,
            List<Integer> genreIds,
            boolean includeAdult
    );
    PageResponse<SearchPersonResponse> searchPeople(
            String query,
            int page,
            int size
    );
    PageResponse<MultiSearchResponse> multiSearch(
            String query,
            int page,
            int size,
            boolean includeAdult
    );
}
