package com.cinetickets.api.repository;

import com.cinetickets.api.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {
    
    Optional<Promotion> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Promotion> findByCinemaIdAndIsActiveTrue(UUID cinemaId);
    
    List<Promotion> findByCinemaIdAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(
            UUID cinemaId, ZonedDateTime now, ZonedDateTime now2);
}