package com.cinetickets.api.service;

import com.cinetickets.api.dto.request.MovieRequest;
import com.cinetickets.api.dto.response.MovieDetailResponse;
import com.cinetickets.api.dto.response.MovieResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MovieService {
    
    List<MovieResponse> getAllCurrentlyShowing();
    
    List<MovieResponse> getAllComingSoon();
    
    MovieDetailResponse getMovieById(UUID id);
    
    Page<MovieResponse> getAllMovies(Pageable pageable);
    
    Page<MovieResponse> searchMovies(String query, Pageable pageable);
    
    UUID createMovie(MovieRequest movieRequest);
    
    void updateMovie(UUID id, MovieRequest movieRequest);
    
    void deleteMovie(UUID id);
}