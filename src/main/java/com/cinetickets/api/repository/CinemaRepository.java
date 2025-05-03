package com.cinetickets.api.repository;

import com.cinetickets.api.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, UUID> {
    
    List<Cinema> findByActiveTrue();
    
    List<Cinema> findByActiveTrueOrderByName();
}