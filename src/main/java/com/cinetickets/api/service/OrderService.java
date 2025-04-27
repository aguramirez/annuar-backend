package com.cinetickets.api.service;

import com.cinetickets.api.dto.request.OrderRequest;
import com.cinetickets.api.dto.response.OrderDetailsResponse;
import com.cinetickets.api.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    
    /**
     * Crea una orden a partir de una reserva existente para un usuario web
     * @param orderRequest Solicitud con los datos de la orden
     * @param userId ID del usuario que realiza la orden
     * @return Respuesta con los detalles básicos de la orden creada
     */
    OrderResponse createOrder(OrderRequest orderRequest, UUID userId);
    
    /**
     * Crea una orden directa para punto de venta (taquilla)
     * @param orderRequest Solicitud con los datos de la orden
     * @param operatorId ID del operador que registra la orden
     * @return Respuesta con los detalles básicos de la orden creada
     */
    OrderResponse createPosOrder(OrderRequest orderRequest, UUID operatorId);
    
    /**
     * Obtiene los detalles completos de una orden
     * @param orderId ID de la orden
     * @param userId ID del usuario (puede ser null para staff)
     * @return Detalles completos de la orden
     */
    OrderDetailsResponse getOrderDetailsById(UUID orderId);
    
    /**
     * Sobrecarga para validar acceso del usuario
     */
    OrderDetailsResponse getOrderDetailsById(UUID orderId, UUID userId);
    
    /**
     * Obtiene los detalles de una orden incluyendo el código QR
     * @param orderId ID de la orden
     * @param userId ID del usuario (puede ser null para staff)
     * @return Detalles completos de la orden con QR
     */
    OrderDetailsResponse getOrderDetailsWithQrById(UUID orderId, UUID userId);
    
    /**
     * Obtiene las órdenes de un usuario paginadas
     * @param userId ID del usuario
     * @param pageable Configuración de paginación
     * @return Página de órdenes
     */
    Page<OrderResponse> getUserOrders(UUID userId, Pageable pageable);
    
    /**
     * Obtiene todas las órdenes (para administradores)
     * @param cinemaId Filtro opcional por cine
     * @param status Filtro opcional por estado
     * @param pageable Configuración de paginación
     * @return Página de órdenes
     */
    Page<OrderResponse> getAllOrders(UUID cinemaId, String status, Pageable pageable);
    
    /**
     * Cancela una orden
     * @param orderId ID de la orden
     */
    void cancelOrder(UUID orderId);
    
    /**
     * Marca una orden como pagada
     * @param orderId ID de la orden
     * @param paymentReference Referencia del pago
     */
    void markOrderAsPaid(UUID orderId, String paymentReference);
}