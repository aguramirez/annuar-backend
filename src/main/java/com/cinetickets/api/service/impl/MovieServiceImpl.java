package com.cinetickets.api.service.impl;

import com.cinetickets.api.dto.request.MovieRequest;
import com.cinetickets.api.dto.response.MovieDetailResponse;
import com.cinetickets.api.dto.response.MovieResponse;
import com.cinetickets.api.entity.Movie;
import com.cinetickets.api.exception.ResourceNotFoundException;
import com.cinetickets.api.repository.MovieRepository;
import com.cinetickets.api.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> getAllCurrentlyShowing() {
        return movieRepository.findAllCurrentlyShowing().stream()
                .map(this::mapToMovieResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> getAllComingSoon() {
        return movieRepository.findAllComingSoon().stream()
                .map(this::mapToMovieResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDetailResponse getMovieById(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
        
        return mapToMovieDetailResponse(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(this::mapToMovieResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> searchMovies(String query, Pageable pageable) {
        return movieRepository.findByTitleContainingIgnoreCase(query, pageable)
                .map(this::mapToMovieResponse);
    }

    @Override
    @Transactional
    public UUID createMovie(MovieRequest movieRequest) {
        Movie movie = new Movie();
        mapMovieRequestToEntity(movieRequest, movie);
        movie.setId(UUID.randomUUID());
        movie.setCreatedAt(ZonedDateTime.now());
        movie.setUpdatedAt(ZonedDateTime.now());
        
        Movie savedMovie = movieRepository.save(movie);
        return savedMovie.getId();
    }

    @Override
    @Transactional
    public void updateMovie(UUID id, MovieRequest movieRequest) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
        
        mapMovieRequestToEntity(movieRequest, movie);
        movie.setUpdatedAt(ZonedDateTime.now());
        
        movieRepository.save(movie);
    }

    @Override
    @Transactional
    public void deleteMovie(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
        
        // Soft delete - cambiar el estado a ARCHIVED en lugar de eliminar físicamente
        movie.setStatus(Movie.MovieStatus.ARCHIVED);
        movie.setUpdatedAt(ZonedDateTime.now());
        
        movieRepository.save(movie);
    }

    private MovieResponse mapToMovieResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .durationMinutes(movie.getDurationMinutes())
                .rating(movie.getRating())
                .posterUrl(movie.getPosterUrl())
                .releaseDate(movie.getReleaseDate())
                .is3d(movie.getIs3d())
                .isSubtitled(movie.getIsSubtitled())
                .language(movie.getLanguage())
                .status(movie.getStatus().name())
                .build();
    }

    private MovieDetailResponse mapToMovieDetailResponse(Movie movie) {
        return MovieDetailResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .synopsis(movie.getSynopsis())
                .durationMinutes(movie.getDurationMinutes())
                .releaseDate(movie.getReleaseDate())
                .endDate(movie.getEndDate())
                .director(movie.getDirector())
                .cast(movie.getCast())
                .genre(movie.getGenre())
                .rating(movie.getRating())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .language(movie.getLanguage())
                .is3d(movie.getIs3d())
                .isSubtitled(movie.getIsSubtitled())
                .status(movie.getStatus().name())
                .build();
    }

    private void mapMovieRequestToEntity(MovieRequest request, Movie movie) {
        movie.setTitle(request.getTitle());
        movie.setSynopsis(request.getSynopsis());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setEndDate(request.getEndDate());
        movie.setDirector(request.getDirector());
        movie.setCast(request.getCast());
        movie.setGenre(request.getGenre());
        movie.setRating(request.getRating());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setLanguage(request.getLanguage());
        movie.setIs3d(request.getIs3d());
        movie.setIsSubtitled(request.getIsSubtitled());
        
        if (request.getStatus() != null) {
            movie.setStatus(Movie.MovieStatus.valueOf(request.getStatus()));
        } else if (movie.getStatus() == null) {
            // Si el estado es nulo (nueva película) y no se proporciona en la solicitud, establecer ACTIVE por defecto
            movie.setStatus(Movie.MovieStatus.ACTIVE);
        }
    }
}