package com.cinetickets.api.repository;

import com.cinetickets.api.entity.Combo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ComboRepository extends JpaRepository<Combo, UUID> {
    
    List<Combo> findByCinemaIdAndIsActiveTrue(UUID cinemaId);
    
    Page<Combo> findByCinemaId(UUID cinemaId, Pageable pageable);
}