package com.cinetickets.api.repository;

import com.cinetickets.api.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShowRepository extends JpaRepository<Show, UUID> {
    
    List<Show> findByMovieIdAndStartTimeGreaterThanEqualOrderByStartTime(UUID movieId, ZonedDateTime startTime);
    
    List<Show> findByRoomIdAndStartTimeBetween(UUID roomId, ZonedDateTime start, ZonedDateTime end);
    
    @Query("SELECT s FROM Show s WHERE s.room.cinema.id = :cinemaId AND s.status = 'SCHEDULED' " +
           "AND s.startTime >= :startTime AND s.startTime <= :endTime ORDER BY s.startTime")
    List<Show> findAllActiveInCinemaForDateRange(UUID cinemaId, ZonedDateTime startTime, ZonedDateTime endTime);
    
    @Query("SELECT s FROM Show s WHERE s.room.cinema.id = :cinemaId AND s.movie.id = :movieId " + 
           "AND s.status = 'SCHEDULED' AND s.startTime >= CURRENT_TIMESTAMP ORDER BY s.startTime")
    List<Show> findAllActiveInCinemaForMovie(UUID cinemaId, UUID movieId);
    
    @Query("SELECT s FROM Show s WHERE s.status = 'SCHEDULED' AND s.startTime >= :startTime " +
           "AND s.startTime <= :endTime ORDER BY s.room.cinema.id, s.startTime")
    List<Show> findAllActiveForDateRange(ZonedDateTime startTime, ZonedDateTime endTime);
    
    @Query("SELECT s FROM Show s " +
           "WHERE s.status = 'SCHEDULED' AND s.startTime <= CURRENT_TIMESTAMP " +
           "AND s.endTime >= CURRENT_TIMESTAMP")
    List<Show> findAllShowsCurrentlyPlaying();
}