package com.cinetickets.api.controller;

import com.cinetickets.api.dto.request.PaymentRequest;
import com.cinetickets.api.dto.response.ApiResponse;
import com.cinetickets.api.dto.response.PaymentResponse;
import com.cinetickets.api.security.UserPrincipal;
import com.cinetickets.api.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    /**
     * Inicia el proceso de pago de una orden
     */
    @PostMapping("/api/orders/{orderId}/pay")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable UUID orderId,
            @Valid @RequestBody PaymentRequest paymentRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        log.info("Processing payment for order: {}", orderId);
        PaymentResponse response = paymentService.processPayment(orderId, paymentRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para recibir notificaciones webhook de MercadoPago
     */
    @PostMapping("/api/payments/webhook")
    public ResponseEntity<ApiResponse> paymentWebhook(@RequestBody String payload) {
        try {
            log.info("Received payment webhook: {}", payload);
            
            JsonNode payloadJson = objectMapper.readTree(payload);
            
            // Verificar tipo de notificación
            String topic = payloadJson.has("topic") ? payloadJson.get("topic").asText() : "";
            String type = payloadJson.has("type") ? payloadJson.get("type").asText() : "";
            
            // Procesar notificación según el tipo
            if ("payment".equals(topic) || "payment".equals(type)) {
                String paymentId = payloadJson.get("data").get("id").asText();
                
                // Obtener estado del pago desde MercadoPago (en una implementación real)
                // Por simplicidad aquí usamos 'approved'
                paymentService.confirmPayment(paymentId, "approved");
            }
            
            return ResponseEntity.ok(new ApiResponse(true, "Webhook processed successfully"));
        } catch (Exception e) {
            log.error("Error processing payment webhook", e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error processing webhook"));
        }
    }

    /**
     * Reembolsa un pago (solo para administradores)
     */
    @PostMapping("/api/admin/orders/{orderId}/refund")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> refundPayment(@PathVariable UUID orderId) {
        paymentService.refundPayment(orderId);
        return ResponseEntity.ok(new ApiResponse(true, "Payment refunded successfully"));
    }
}