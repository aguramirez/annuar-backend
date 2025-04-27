package com.cinetickets.api.controller;

import com.cinetickets.api.dto.request.TicketValidationRequest;
import com.cinetickets.api.dto.response.ApiResponse;
import com.cinetickets.api.dto.response.OrderDetailsResponse;
import com.cinetickets.api.dto.response.ShowResponse;
import com.cinetickets.api.service.OrderService;
import com.cinetickets.api.service.QrCodeService;
import com.cinetickets.api.service.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/gate")
@PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
@RequiredArgsConstructor
public class GateController {

    private final QrCodeService qrCodeService;
    private final OrderService orderService;
    private final ShowService showService;

    /**
     * Valida un código QR de entrada
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse> validateTicket(
            @Valid @RequestBody TicketValidationRequest request) {
        
        log.info("Validating ticket: {}", request.getQrContent());
        
        // Validar el código QR
        boolean isValid = qrCodeService.validateQrCode(request.getQrContent());
        
        if (!isValid) {
            return ResponseEntity.ok(new ApiResponse(false, "Invalid QR code"));
        }
        
        // Actualizar estado de uso del ticket si es necesario
        String[] parts = request.getQrContent().split("\\|");
        if (parts.length >= 2) {
            try {
                String orderId = parts[0];
                // Aquí se podría implementar la lógica para marcar como usado
                
                return ResponseEntity.ok(new ApiResponse(true, "Valid ticket"));
            } catch (Exception e) {
                log.error("Error validating ticket", e);
                return ResponseEntity.ok(new ApiResponse(false, "Error validating ticket"));
            }
        }
        
        return ResponseEntity.ok(new ApiResponse(false, "Invalid QR format"));
    }

    /**
     * Obtiene los detalles de una orden (para verificación manual)
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDetailsResponse> getOrderDetails(@PathVariable String orderId) {
        OrderDetailsResponse order = orderService.getOrderDetailsById(java.util.UUID.fromString(orderId));
        return ResponseEntity.ok(order);
    }

    /**
     * Obtiene las funciones que se están reproduciendo actualmente (para validación en puerta)
     */
    @GetMapping("/shows/current")
    public ResponseEntity<List<ShowResponse>> getCurrentlyPlayingShows() {
        List<ShowResponse> shows = showService.getCurrentlyPlayingShows();
        return ResponseEntity.ok(shows);
    }
}