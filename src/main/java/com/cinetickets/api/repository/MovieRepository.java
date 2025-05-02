package com.cinetickets.api.repository;

import com.cinetickets.api.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {

    List<Movie> findByStatus(Movie.MovieStatus status);
    
    @Query("SELECT m FROM Movie m WHERE m.status = 'ACTIVE' AND " +
           "(m.releaseDate IS NULL OR m.releaseDate <= CURRENT_DATE) AND " +
           "(m.endDate IS NULL OR m.endDate >= CURRENT_DATE)")
    List<Movie> findAllCurrentlyShowing();
    
    @Query("SELECT m FROM Movie m WHERE m.status = 'UPCOMING' OR " +
           "(m.status = 'ACTIVE' AND m.releaseDate > CURRENT_DATE)")
    List<Movie> findAllComingSoon();
    
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    @Query("SELECT DISTINCT m FROM Movie m JOIN Show s ON m.id = s.movie.id " +
           "WHERE s.room.cinema.id = :cinemaId AND s.startTime >= CURRENT_TIMESTAMP " +
           "AND m.status = 'ACTIVE' ORDER BY s.startTime")
    List<Movie> findAllActiveInCinema(UUID cinemaId);
}