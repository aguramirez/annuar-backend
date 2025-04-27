package com.cinetickets.api.service;

import com.cinetickets.api.dto.request.ReservationRequest;
import com.cinetickets.api.dto.response.ReservationResponse;

import java.util.UUID;

public interface ReservationService {
    
    /**
     * Crea una reserva temporal para un usuario (web)
     * @param request Solicitud de reserva con showId y asientos
     * @param userId ID del usuario (puede ser null para usuarios anónimos)
     * @return Respuesta con los detalles de la reserva
     */
    ReservationResponse createReservation(ReservationRequest request, UUID userId);
    
    /**
     * Crea una reserva para punto de venta (taquilla)
     * @param request Solicitud de reserva
     * @param operatorId ID del operador que realiza la reserva
     * @return Respuesta con los detalles de la reserva
     */
    ReservationResponse createPosReservation(ReservationRequest request, UUID operatorId);
    
    /**
     * Obtiene una reserva por su ID
     * @param id ID de la reserva
     * @param userId ID del usuario (puede ser null para staff)
     * @return Respuesta con los detalles de la reserva
     */
    ReservationResponse getReservationById(UUID id, UUID userId);
    
    /**
     * Cancela una reserva
     * @param id ID de la reserva
     * @param userId ID del usuario (puede ser null para staff)
     */
    void cancelReservation(UUID id, UUID userId);
    
    /**
     * Confirma una reserva (cuando se realiza el pago)
     * @param id ID de la reserva
     */
    void confirmReservation(UUID id);
    
    /**
     * Verifica y expira las reservas vencidas
     */
    void expireReservations();
}