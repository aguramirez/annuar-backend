package com.cinetickets.api.repository;

import com.cinetickets.api.entity.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, UUID> {
    
    List<TicketType> findByCinemaIdAndIsActiveTrue(UUID cinemaId);
}