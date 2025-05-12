package com.cinetickets.api.repository;

import com.cinetickets.api.entity.Combo;
import com.cinetickets.api.entity.Promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ComboRepository extends JpaRepository<Combo, UUID> {
    List<Promotion> findByIsActiveTrue();
    List<Promotion> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
            ZonedDateTime now, ZonedDateTime now2);
}