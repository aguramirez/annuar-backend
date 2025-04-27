package com.cinetickets.api.repository;

import com.cinetickets.api.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {
    
    List<Seat> findByRoomId(UUID roomId);
    
    List<Seat> findByRoomIdAndStatus(UUID roomId, Seat.SeatStatus status);

    @Query("SELECT rs.seat.id FROM ReservedSeat rs " +
           "WHERE rs.reservation.show.id = :showId " +
           "AND rs.reservation.status IN ('PENDING', 'CONFIRMED')")
    List<UUID> findReservedSeatIdsForShow(UUID showId);
}