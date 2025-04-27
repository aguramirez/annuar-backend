package com.cinetickets.api.controller;

import com.cinetickets.api.dto.request.ReservationRequest;
import com.cinetickets.api.dto.response.ApiResponse;
import com.cinetickets.api.dto.response.ReservationResponse;
import com.cinetickets.api.security.UserPrincipal;
import com.cinetickets.api.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest reservationRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        UUID userId = currentUser != null ? currentUser.getId() : null;
        ReservationResponse reservation = reservationService.createReservation(reservationRequest, userId);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/reservations/{id}")
                .buildAndExpand(reservation.getId()).toUri();
        
        return ResponseEntity.created(location).body(reservation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        UUID userId = currentUser != null ? currentUser.getId() : null;
        ReservationResponse reservation = reservationService.getReservationById(id, userId);
        
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> cancelReservation(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        UUID userId = currentUser != null ? currentUser.getId() : null;
        reservationService.cancelReservation(id, userId);
        
        return ResponseEntity.ok(new ApiResponse(true, "Reservation cancelled successfully"));
    }

    // Endpoint para punto de venta (taquilla)
    @PostMapping("/pos")
    public ResponseEntity<ReservationResponse> createPosReservation(
            @Valid @RequestBody ReservationRequest reservationRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        // Validar que el usuario tenga rol STAFF o ADMIN
        if (!currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("STAFF") || a.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        
        ReservationResponse reservation = reservationService.createPosReservation(
                reservationRequest, 
                currentUser.getId() // ID del operador
        );
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/reservations/{id}")
                .buildAndExpand(reservation.getId()).toUri();
        
        return ResponseEntity.created(location).body(reservation);
    }
}