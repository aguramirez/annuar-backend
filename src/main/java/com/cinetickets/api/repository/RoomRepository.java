package com.cinetickets.api.repository;

import com.cinetickets.api.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    
    List<Room> findByCinemaId(UUID cinemaId);
    
    List<Room> findByCinemaIdAndStatus(UUID cinemaId, Room.RoomStatus status);
    
    Optional<Room> findByCinemaIdAndName(UUID cinemaId, String name);
}