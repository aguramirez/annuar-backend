package com.cinetickets.api.config;

import com.cinetickets.api.service.PaymentService;
import com.cinetickets.api.service.mock.MockPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Configuración específica para el entorno de desarrollo.
 * Permite reemplazar servicios reales con simulaciones.
 */
@Slf4j
@Configuration
@Profile({"dev", "test"})
public class DevConfig {

    /**
     * Registra el inicio del entorno de desarrollo.
     */
    @Bean
    public void logDevEnvironment() {
        log.info("==================================================");
        log.info("RUNNING IN DEVELOPMENT MODE");
        log.info("External services are mocked for local testing");
        log.info("==================================================");
    }

    /**
     * Reemplaza el servicio de pago real con una implementación simulada.
     */
    @Bean
    @Primary
    @Profile({"dev", "test"})
    public PaymentService mockPaymentService(MockPaymentService mockService) {
        log.info("Using MOCK Payment Service for development");
        return new PaymentService(null, null, null) {
            @Override
            public com.cinetickets.api.dto.response.PaymentResponse processPayment(
                    java.util.UUID orderId, 
                    com.cinetickets.api.dto.request.PaymentRequest paymentRequest) {
                return mockService.processPayment(orderId, paymentRequest);
            }
            
            @Override
            public void confirmPayment(String paymentId, String status) {
                mockService.confirmPayment(paymentId, status);
            }
            
            @Override
            public void refundPayment(java.util.UUID orderId) {
                mockService.refundPayment(orderId);
            }
        };
    }
}