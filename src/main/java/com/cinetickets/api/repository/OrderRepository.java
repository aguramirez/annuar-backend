package com.cinetickets.api.repository;

import com.cinetickets.api.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    Page<Order> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    Optional<Order> findByIdAndUserId(UUID id, UUID userId);
    
    Optional<Order> findByReservationId(UUID reservationId);
    
    Optional<Order> findByQrCode(String qrCode);
    
    @Query("SELECT o FROM Order o WHERE o.reservation.show.room.cinema.id = :cinemaId " +
           "AND o.createdAt BETWEEN :startDate AND :endDate AND o.status = 'COMPLETED'")
    List<Order> findCompletedOrdersByCinemaAndDateRange(UUID cinemaId, ZonedDateTime startDate, ZonedDateTime endDate);
    
    @Query("SELECT SUM(o.total) FROM Order o WHERE o.reservation.show.room.cinema.id = :cinemaId " +
           "AND o.createdAt BETWEEN :startDate AND :endDate AND o.status = 'COMPLETED' AND o.paymentStatus = 'PAID'")
    Optional<BigDecimal> calculateTotalSalesByCinemaAndDateRange(UUID cinemaId, ZonedDateTime startDate, ZonedDateTime endDate);
    
    @Query(value = "SELECT EXTRACT(DOW FROM o.created_at) as day_of_week, COUNT(*) as order_count " +
           "FROM orders o " +
           "JOIN reservations r ON o.reservation_id = r.id " +
           "JOIN shows s ON r.show_id = s.id " +
           "JOIN rooms rm ON s.room_id = rm.id " +
           "WHERE rm.cinema_id = :cinemaId AND o.status = 'COMPLETED' " +
           "AND o.created_at BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(DOW FROM o.created_at) " +
           "ORDER BY order_count DESC", nativeQuery = true)
    List<Object[]> findMostPopularDaysOfWeek(UUID cinemaId, ZonedDateTime startDate, ZonedDateTime endDate);
}