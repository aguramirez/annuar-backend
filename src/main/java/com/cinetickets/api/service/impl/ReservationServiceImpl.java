package com.cinetickets.api.service.impl;

import com.cinetickets.api.dto.request.ReservationRequest;
import com.cinetickets.api.dto.response.ReservationResponse;
import com.cinetickets.api.dto.response.ShowResponse;
import com.cinetickets.api.entity.*;
import com.cinetickets.api.exception.ResourceNotFoundException;
import com.cinetickets.api.exception.SeatUnavailableException;
import com.cinetickets.api.repository.*;
import com.cinetickets.api.service.ReservationService;
import com.cinetickets.api.service.ShowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final UserRepository userRepository;
    private final ShowService showService;
    
    @Value("${app.reservation.expiration-minutes:15}")
    private int reservationExpirationMinutes;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, UUID userId) {
        // Buscar la función
        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", request.getShowId()));
        
        // Verificar que la función no haya empezado
        if (show.getStartTime().isBefore(ZonedDateTime.now())) {
            throw new IllegalArgumentException("Cannot reserve seats for a show that has already started");
        }
        
        // Verificar que los asientos estén disponibles
        validateSeatsAvailability(show.getId(), request.getSeats().stream()
                .map(ReservationRequest.SeatSelectionRequest::getSeatId)
                .collect(Collectors.toList()));
        
        // Crear la reserva
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        
        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .user(user)
                .show(show)
                .status(Reservation.ReservationStatus.PENDING)
                .expiresAt(ZonedDateTime.now().plusMinutes(reservationExpirationMinutes))
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        
        // Crear las reservas de asientos
        List<ReservedSeat> reservedSeats = new ArrayList<>();
        for (ReservationRequest.SeatSelectionRequest seatRequest : request.getSeats()) {
            Seat seat = seatRepository.findById(seatRequest.getSeatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seat", "id", seatRequest.getSeatId()));
            
            TicketType ticketType = ticketTypeRepository.findById(seatRequest.getTicketTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TicketType", "id", seatRequest.getTicketTypeId()));
            
            ReservedSeat reservedSeat = ReservedSeat.builder()
                    .id(UUID.randomUUID())
                    .reservation(reservation)
                    .seat(seat)
                    .ticketType(ticketType)
                    .price(ticketType.getPrice())
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build();
            
            reservedSeats.add(reservedSeat);
        }
        
        reservation.setReservedSeats(reservedSeats);
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Mapear la respuesta
        return mapToReservationResponse(savedReservation);
    }

    @Override
    @Transactional
    public ReservationResponse createPosReservation(ReservationRequest request, UUID operatorId) {
        // La lógica es similar a createReservation pero sin tiempo de expiración
        // y confirmación automática (ya que es una venta directa en taquilla)
        
        // Buscar la función
        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", request.getShowId()));
        
        // Verificar que la función no haya empezado
        if (show.getStartTime().isBefore(ZonedDateTime.now())) {
            throw new IllegalArgumentException("Cannot reserve seats for a show that has already started");
        }
        
        // Verificar que los asientos estén disponibles
        validateSeatsAvailability(show.getId(), request.getSeats().stream()
                .map(ReservationRequest.SeatSelectionRequest::getSeatId)
                .collect(Collectors.toList()));
        
        // Crear la reserva (sin usuario asignado, es una venta anónima en taquilla)
        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .show(show)
                .status(Reservation.ReservationStatus.CONFIRMED) // Confirmado inmediatamente
                .expiresAt(show.getStartTime()) // Expira al inicio de la función
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        
        // Crear las reservas de asientos
        List<ReservedSeat> reservedSeats = new ArrayList<>();
        for (ReservationRequest.SeatSelectionRequest seatRequest : request.getSeats()) {
            Seat seat = seatRepository.findById(seatRequest.getSeatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seat", "id", seatRequest.getSeatId()));
            
            TicketType ticketType = ticketTypeRepository.findById(seatRequest.getTicketTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TicketType", "id", seatRequest.getTicketTypeId()));
            
            ReservedSeat reservedSeat = ReservedSeat.builder()
                    .id(UUID.randomUUID())
                    .reservation(reservation)
                    .seat(seat)
                    .ticketType(ticketType)
                    .price(ticketType.getPrice())
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build();
            
            reservedSeats.add(reservedSeat);
        }
        
        reservation.setReservedSeats(reservedSeats);
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Mapear la respuesta
        return mapToReservationResponse(savedReservation);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(UUID id, UUID userId) {
        Reservation reservation;
        
        if (userId != null) {
            // Usuario normal solo puede ver sus propias reservas
            reservation = reservationRepository.findByIdAndUserId(id, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        } else {
            // Personal del cine puede ver cualquier reserva
            reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        }
        
        return mapToReservationResponse(reservation);
    }

    @Override
    @Transactional
    public void cancelReservation(UUID id, UUID userId) {
        Reservation reservation;
        
        if (userId != null) {
            // Usuario normal solo puede cancelar sus propias reservas
            reservation = reservationRepository.findByIdAndUserId(id, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        } else {
            // Personal del cine puede cancelar cualquier reserva
            reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        }
        
        // Solo se pueden cancelar reservas pendientes
        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new IllegalStateException("Only pending reservations can be cancelled");
        }
        
        reservation.setStatus(Reservation.ReservationStatus.CANCELED);
        reservation.setUpdatedAt(ZonedDateTime.now());
        
        reservationRepository.save(reservation);
        
        log.info("Reservation {} has been cancelled", id);
    }

    @Override
    @Transactional
    public void confirmReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        
        // Solo se pueden confirmar reservas pendientes
        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new IllegalStateException("Only pending reservations can be confirmed");
        }
        
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservation.setUpdatedAt(ZonedDateTime.now());
        
        reservationRepository.save(reservation);
        
        log.info("Reservation {} has been confirmed", id);
    }

    @Override
    @Transactional
    public void expireReservations() {
        ZonedDateTime now = ZonedDateTime.now();
        int expiredCount = reservationRepository.expireReservations(now);
        
        if (expiredCount > 0) {
            log.info("Expired {} reservations", expiredCount);
        }
    }
    
    /**
     * Verifica que los asientos estén disponibles para la función
     */
    private void validateSeatsAvailability(UUID showId, List<UUID> seatIds) {
        List<UUID> reservedSeats = reservationRepository.findReservedSeatIdsForShow(showId);
        
        for (UUID seatId : seatIds) {
            if (reservedSeats.contains(seatId)) {
                throw new SeatUnavailableException("Seat is already reserved");
            }
        }
    }
    
    /**
     * Mapea una entidad Reservation a un DTO ReservationResponse
     */
    private ReservationResponse mapToReservationResponse(Reservation reservation) {
        // Mapear show
        ShowResponse showResponse = showService.getShowById(reservation.getShow().getId());
        
        // Mapear asientos reservados
        List<ReservationResponse.ReservedSeatDTO> seatDTOs = reservation.getReservedSeats().stream()
                .map(rs -> ReservationResponse.ReservedSeatDTO.builder()
                        .id(rs.getId())
                        .seatId(rs.getSeat().getId())
                        .row(rs.getSeat().getRow())
                        .number(rs.getSeat().getNumber())
                        .ticketType(rs.getTicketType().getName())
                        .price(rs.getPrice())
                        .build())
                .collect(Collectors.toList());
        
        // Calcular monto total
        BigDecimal totalAmount = reservation.getReservedSeats().stream()
                .map(ReservedSeat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return ReservationResponse.builder()
                .id(reservation.getId())
                .userId(reservation.getUser() != null ? reservation.getUser().getId() : null)
                .show(showResponse)
                .seats(seatDTOs)
                .expiresAt(reservation.getExpiresAt())
                .status(reservation.getStatus().name())
                .createdAt(reservation.getCreatedAt())
                .totalAmount(totalAmount)
                .build();
    }
}