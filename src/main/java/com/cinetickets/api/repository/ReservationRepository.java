package com.cinetickets.api.repository;

import com.cinetickets.api.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    
    List<Reservation> findByUserIdAndStatusIn(UUID userId, List<Reservation.ReservationStatus> statuses);
    
    Optional<Reservation> findByIdAndUserId(UUID id, UUID userId);
    
    List<Reservation> findByShowIdAndStatusIn(UUID showId, List<Reservation.ReservationStatus> statuses);
    
    @Query("SELECT r FROM Reservation r WHERE r.expiresAt < :now AND r.status = 'PENDING'")
    List<Reservation> findExpiredReservations(ZonedDateTime now);
    
    @Modifying
    @Query("UPDATE Reservation r SET r.status = 'EXPIRED' WHERE r.expiresAt < :now AND r.status = 'PENDING'")
    int expireReservations(ZonedDateTime now);
    
    @Query("SELECT COUNT(rs) FROM ReservedSeat rs WHERE rs.reservation.show.id = :showId " +
           "AND rs.seat.id = :seatId AND rs.reservation.status IN ('PENDING', 'CONFIRMED')")
    long countReservationsForSeatInShow(UUID showId, UUID seatId);
    
    @Query("SELECT DISTINCT rs.seat.id FROM ReservedSeat rs " +
           "WHERE rs.reservation.show.id = :showId AND rs.reservation.status IN ('PENDING', 'CONFIRMED')")
    List<UUID> findReservedSeatIdsForShow(UUID showId);
}