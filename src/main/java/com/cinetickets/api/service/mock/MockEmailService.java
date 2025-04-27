package com.cinetickets.api.service.mock;

import com.cinetickets.api.dto.response.OrderDetailsResponse;
import com.cinetickets.api.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementación simulada del servicio de email para desarrollo.
 * Esta clase se activa solo con el perfil "dev" o "test".
 */
@Slf4j
@Service
@Profile({"dev", "test"})
public class MockEmailService {

    /**
     * Simula el envío de un correo electrónico de bienvenida
     */
    public void sendWelcomeEmail(User user) {
        log.info("MOCK EMAIL: Sending welcome email to {}", user.getEmail());
        log.info("  - Subject: Welcome to CineTickets!");
        log.info("  - Recipient: {} {}", user.getFirstName(), user.getLastName());
    }

    /**
     * Simula el envío de un correo con confirmación de compra y entradas
     */
    public void sendTicketConfirmationEmail(User user, OrderDetailsResponse order) {
        log.info("MOCK EMAIL: Sending ticket confirmation to {}", user.getEmail());
        log.info("  - Subject: Your CineTickets Purchase Confirmation");
        log.info("  - Recipient: {} {}", user.getFirstName(), user.getLastName());
        log.info("  - Order ID: {}", order.getId());
        log.info("  - Movie: {}", order.getShowDetails().getMovieTitle());
        log.info("  - Date: {}", order.getShowDetails().getStartTime());
        log.info("  - Cinema: {}", order.getShowDetails().getCinemaName());
        log.info("  - Room: {}", order.getShowDetails().getRoomName());
        log.info("  - Seats: {}", order.getReservedSeats().size());
        log.info("  - Total: {}", order.getTotal());
    }

    /**
     * Simula el envío de un correo para recuperación de contraseña
     */
    public void sendPasswordResetEmail(User user, String token) {
        log.info("MOCK EMAIL: Sending password reset email to {}", user.getEmail());
        log.info("  - Subject: Reset Your CineTickets Password");
        log.info("  - Recipient: {} {}", user.getFirstName(), user.getLastName());
        log.info("  - Reset Token: {}", token);
    }

    /**
     * Simula el envío de una encuesta post-servicio
     */
    public void sendSurveyEmail(User user, UUID orderId) {
        log.info("MOCK EMAIL: Sending survey email to {}", user.getEmail());
        log.info("  - Subject: How was your experience?");
        log.info("  - Recipient: {} {}", user.getFirstName(), user.getLastName());
        log.info("  - Order ID: {}", orderId);
    }
}