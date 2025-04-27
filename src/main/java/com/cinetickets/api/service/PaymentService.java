package com.cinetickets.api.service;

import com.cinetickets.api.dto.request.PaymentRequest;
import com.cinetickets.api.dto.response.PaymentResponse;
import com.cinetickets.api.entity.Order;
import com.cinetickets.api.exception.PaymentProcessingException;
import com.cinetickets.api.repository.OrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${mercadopago.access-token}")
    private String mercadoPagoAccessToken;
    
    @Value("${app.payment.success-url}")
    private String successUrl;
    
    @Value("${app.payment.failure-url}")
    private String failureUrl;
    
    @Value("${app.payment.pending-url}")
    private String pendingUrl;

    /**
     * Procesa un pago a través de MercadoPago
     */
    @Transactional
    public PaymentResponse processPayment(UUID orderId, PaymentRequest paymentRequest) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new PaymentProcessingException("Order not found"));
            
            // Validar que la orden no esté ya pagada
            if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
                throw new PaymentProcessingException("Order is already paid");
            }
            
            // Construir la solicitud a MercadoPago
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + mercadoPagoAccessToken);
            
            ObjectNode requestBody = objectMapper.createObjectNode();
            
            // Datos de la transacción
            requestBody.put("transaction_amount", order.getTotal().doubleValue());
            requestBody.put("description", "Boletos CineTickets - Orden #" + order.getId());
            requestBody.put("payment_method_id", paymentRequest.getPaymentMethodId());
            
            // Datos del pagador
            ObjectNode payer = requestBody.putObject("payer");
            payer.put("email", paymentRequest.getPayerEmail());
            
            // URLs de redirección
            ObjectNode redirectUrls = requestBody.putObject("redirect_urls");
            redirectUrls.put("success", successUrl + "?order_id=" + orderId);
            redirectUrls.put("failure", failureUrl + "?order_id=" + orderId);
            redirectUrls.put("pending", pendingUrl + "?order_id=" + orderId);
            
            // Datos adicionales de la tarjeta si es pago con tarjeta
            if (paymentRequest.getCardToken() != null) {
                requestBody.put("token", paymentRequest.getCardToken());
                requestBody.put("installments", paymentRequest.getInstallments());
            }
            
            // Realizar la solicitud HTTP a MercadoPago
            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            String responseBody = restTemplate.postForObject(
                    "https://api.mercadopago.com/v1/payments", 
                    request, 
                    String.class
            );
            
            // Procesar la respuesta
            JsonNode responseJson = objectMapper.readTree(responseBody);
            String status = responseJson.get("status").asText();
            String paymentId = responseJson.get("id").asText();
            
            // Actualizar la orden con los datos del pago
            updateOrderPaymentStatus(order, status, paymentId);
            
            PaymentResponse response = new PaymentResponse();
            response.setStatus(status);
            response.setPaymentId(paymentId);
            response.setRedirectUrl(getRedirectUrlByStatus(status, orderId));
            
            return response;
            
        } catch (Exception e) {
            log.error("Error processing payment for order {}", orderId, e);
            throw new PaymentProcessingException("Error processing payment: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza el estado de pago de una orden según la respuesta de MercadoPago
     */
    private void updateOrderPaymentStatus(Order order, String mercadoPagoStatus, String paymentId) {
        Order.PaymentStatus paymentStatus;
        
        switch (mercadoPagoStatus) {
            case "approved":
                paymentStatus = Order.PaymentStatus.PAID;
                order.setStatus(Order.OrderStatus.COMPLETED);
                break;
            case "in_process":
            case "pending":
                paymentStatus = Order.PaymentStatus.PENDING;
                break;
            case "rejected":
            case "cancelled":
            case "refunded":
                paymentStatus = Order.PaymentStatus.FAILED;
                break;
            default:
                paymentStatus = Order.PaymentStatus.PENDING;
        }
        
        order.setPaymentStatus(paymentStatus);
        order.setPaymentReference(paymentId);
        orderRepository.save(order);
    }
    
    /**
     * Obtiene la URL de redirección según el estado del pago
     */
    private String getRedirectUrlByStatus(String status, UUID orderId) {
        switch (status) {
            case "approved":
                return successUrl + "?order_id=" + orderId;
            case "in_process":
            case "pending":
                return pendingUrl + "?order_id=" + orderId;
            default:
                return failureUrl + "?order_id=" + orderId;
        }
    }
    
    /**
     * Confirma un pago basado en una notificación webhook de MercadoPago
     */
    @Transactional
    public void confirmPayment(String paymentId, String status) {
        try {
            Order order = orderRepository.findByPaymentReference(paymentId)
                    .orElseThrow(() -> new PaymentProcessingException("Order not found for payment ID: " + paymentId));
            
            updateOrderPaymentStatus(order, status, paymentId);
            
            log.info("Payment {} confirmed with status: {}", paymentId, status);
        } catch (Exception e) {
            log.error("Error confirming payment {}", paymentId, e);
            throw new PaymentProcessingException("Error confirming payment: " + e.getMessage());
        }
    }
    
    /**
     * Reembolsa un pago
     */
    @Transactional
    public void refundPayment(UUID orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new PaymentProcessingException("Order not found"));
            
            if (order.getPaymentStatus() != Order.PaymentStatus.PAID) {
                throw new PaymentProcessingException("Cannot refund an unpaid order");
            }
            
            // Solicitud de reembolso a MercadoPago
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + mercadoPagoAccessToken);
            
            HttpEntity<String> request = new HttpEntity<>("{}", headers);
            restTemplate.postForObject(
                    "https://api.mercadopago.com/v1/payments/" + order.getPaymentReference() + "/refunds",
                    request,
                    String.class
            );
            
            // Actualizar estado de la orden
            order.setPaymentStatus(Order.PaymentStatus.REFUNDED);
            order.setStatus(Order.OrderStatus.CANCELED);
            orderRepository.save(order);
            
            log.info("Payment for order {} refunded", orderId);
        } catch (Exception e) {
            log.error("Error refunding payment for order {}", orderId, e);
            throw new PaymentProcessingException("Error refunding payment: " + e.getMessage());
        }
    }
}