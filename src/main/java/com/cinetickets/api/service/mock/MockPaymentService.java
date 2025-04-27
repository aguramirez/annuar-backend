package com.cinetickets.api.service.mock;

import com.cinetickets.api.dto.request.PaymentRequest;
import com.cinetickets.api.dto.response.PaymentResponse;
import com.cinetickets.api.entity.Order;
import com.cinetickets.api.exception.PaymentProcessingException;
import com.cinetickets.api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementación simulada del servicio de pagos para desarrollo y pruebas.
 * Esta clase se activa solo con el perfil "dev" o "test".
 */
@Slf4j
@Service
@Profile({"dev", "test"})
@RequiredArgsConstructor
public class MockPaymentService {

    private final OrderRepository orderRepository;

    /**
     * Procesa un pago simulado, siempre devuelve éxito
     */
    @Transactional
    public PaymentResponse processPayment(UUID orderId, PaymentRequest paymentRequest) {
        try {
            log.info("MOCK: Processing payment for order {}", orderId);
            
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new PaymentProcessingException("Order not found"));
            
            // Validar que la orden no esté ya pagada
            if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
                throw new PaymentProcessingException("Order is already paid");
            }
            
            // Actualizar orden como pagada
            order.setPaymentStatus(Order.PaymentStatus.PAID);
            order.setStatus(Order.OrderStatus.COMPLETED);
            order.setPaymentReference("MOCK-" + UUID.randomUUID().toString().substring(0, 8));
            
            orderRepository.save(order);
            
            // Preparar respuesta
            String redirectUrl = "http://localhost:3000/payment/success?order_id=" + orderId;
            
            PaymentResponse response = new PaymentResponse();
            response.setStatus("approved");
            response.setPaymentId("MOCK-PAYMENT-" + System.currentTimeMillis());
            response.setRedirectUrl(redirectUrl);
            response.setMessage("Mock payment processed successfully");
            
            return response;
            
        } catch (Exception e) {
            log.error("MOCK: Error processing payment for order {}", orderId, e);
            throw new PaymentProcessingException("Error processing payment: " + e.getMessage());
        }
    }
    
    /**
     * Confirma un pago simulado
     */
    @Transactional
    public void confirmPayment(String paymentId, String status) {
        log.info("MOCK: Confirming payment {} with status: {}", paymentId, status);
    }
    
    /**
     * Reembolsa un pago simulado
     */
    @Transactional
    public void refundPayment(UUID orderId) {
        try {
            log.info("MOCK: Refunding payment for order {}", orderId);
            
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new PaymentProcessingException("Order not found"));
            
            order.setPaymentStatus(Order.PaymentStatus.REFUNDED);
            order.setStatus(Order.OrderStatus.CANCELED);
            orderRepository.save(order);
            
        } catch (Exception e) {
            log.error("MOCK: Error refunding payment for order {}", orderId, e);
            throw new PaymentProcessingException("Error refunding payment: " + e.getMessage());
        }
    }
}