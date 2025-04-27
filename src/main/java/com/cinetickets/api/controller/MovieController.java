package com.cinetickets.api.controller;

import com.cinetickets.api.dto.request.MovieRequest;
import com.cinetickets.api.dto.response.ApiResponse;
import com.cinetickets.api.dto.response.MovieDetailResponse;
import com.cinetickets.api.dto.response.MovieResponse;
import com.cinetickets.api.dto.response.ShowResponse;
import com.cinetickets.api.service.MovieService;
import com.cinetickets.api.service.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final ShowService showService;

    // Endpoints públicos - no requieren autenticación

    @GetMapping("/api/movies")
    public ResponseEntity<List<MovieResponse>> getAllMoviesCurrentlyShowing() {
        List<MovieResponse> movies = movieService.getAllCurrentlyShowing();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/api/movies/coming-soon")
    public ResponseEntity<List<MovieResponse>> getAllComingSoonMovies() {
        List<MovieResponse> movies = movieService.getAllComingSoon();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/api/movies/{id}")
    public ResponseEntity<MovieDetailResponse> getMovieById(@PathVariable UUID id) {
        MovieDetailResponse movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/api/movies/{id}/shows")
    public ResponseEntity<List<ShowResponse>> getShowsForMovie(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(required = false) String date) {
        
        List<ShowResponse> shows;
        if (cinemaId != null) {
            shows = showService.getShowsForMovieInCinema(id, cinemaId, date);
        } else {
            shows = showService.getShowsForMovie(id, date);
        }
        
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/api/movies/search")
    public ResponseEntity<Page<MovieResponse>> searchMovies(
            @RequestParam String query,
            Pageable pageable) {
        
        Page<MovieResponse> movies = movieService.searchMovies(query, pageable);
        return ResponseEntity.ok(movies);
    }

    // Endpoints administrativos - requieren autenticación y rol ADMIN

    @GetMapping("/api/admin/movies")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<MovieResponse>> getAllMoviesAdmin(Pageable pageable) {
        Page<MovieResponse> movies = movieService.getAllMovies(pageable);
        return ResponseEntity.ok(movies);
    }

    @PostMapping("/api/admin/movies")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> createMovie(@Valid @RequestBody MovieRequest movieRequest) {
        UUID movieId = movieService.createMovie(movieRequest);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/movies/{id}")
                .buildAndExpand(movieId).toUri();
        
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Movie created successfully"));
    }

    @PutMapping("/api/admin/movies/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> updateMovie(
            @PathVariable UUID id,
            @Valid @RequestBody MovieRequest movieRequest) {
        
        movieService.updateMovie(id, movieRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Movie updated successfully"));
    }

    @DeleteMapping("/api/admin/movies/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> deleteMovie(@PathVariable UUID id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(new ApiResponse(true, "Movie deleted successfully"));
    }
}