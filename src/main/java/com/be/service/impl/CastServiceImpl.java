package com.be.service.impl;

import com.be.appexception.ResourceNotFoundException;
import com.be.model.dto.CastDetailDTO;
import com.be.model.dto.MovieDTO;
import com.be.model.entity.Cast;
import com.be.repository.CastRepository;
import com.be.repository.MovieCastRepository;
import com.be.service.CastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CastServiceImpl implements CastService {
    private final CastRepository castRepository;
    private final MovieCastRepository movieCastRepository;

    public CastServiceImpl(CastRepository castRepository, MovieCastRepository movieCastRepository) {
        this.castRepository = castRepository;
        this.movieCastRepository = movieCastRepository;
    }

    @Override
    public CastDetailDTO getCastDetails(Long castId) {
        Cast cast = castRepository.findById(castId)
                .orElseThrow(() -> new ResourceNotFoundException("Cast not found"));

        return CastDetailDTO.fromEntity(cast);
    }


    @Override
    public Page<MovieDTO> getCastMovies(Long castId, Pageable pageable) {
        Cast cast = castRepository.findById(castId)
                .orElseThrow(() -> new ResourceNotFoundException("Cast not found"));

        return movieCastRepository.findByCastOrderByMovieReleaseDateDesc(cast, pageable)
                .map(movieCast -> MovieDTO.fromEntity(movieCast.getMovie(), null));
    }
}
