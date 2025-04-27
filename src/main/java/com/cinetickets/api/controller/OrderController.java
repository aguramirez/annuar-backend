package com.cinetickets.api.controller;

import com.cinetickets.api.dto.request.OrderRequest;
import com.cinetickets.api.dto.response.ApiResponse;
import com.cinetickets.api.dto.response.OrderDetailsResponse;
import com.cinetickets.api.dto.response.OrderResponse;
import com.cinetickets.api.security.UserPrincipal;
import com.cinetickets.api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Crea una nueva orden a partir de una reserva existente
     */
    @PostMapping("/api/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        UUID userId = currentUser != null ? currentUser.getId() : null;
        OrderResponse order = orderService.createOrder(orderRequest, userId);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/orders/{id}")
                .buildAndExpand(order.getId()).toUri();
        
        return ResponseEntity.created(location).body(order);
    }

    /**
     * Obtiene los detalles de una orden
     */
    @GetMapping("/api/orders/{id}")
    public ResponseEntity<OrderDetailsResponse> getOrderDetails(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        UUID userId = currentUser != null ? currentUser.getId() : null;
        OrderDetailsResponse order = orderService.getOrderDetailsById(id, userId);
        
        return ResponseEntity.ok(order);
    }

    /**
     * Obtiene el código QR de una entrada
     */
    @GetMapping("/api/orders/{id}/tickets")
    public ResponseEntity<OrderDetailsResponse> getOrderTickets(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        UUID userId = currentUser != null ? currentUser.getId() : null;
        OrderDetailsResponse order = orderService.getOrderDetailsWithQrById(id, userId);
        
        return ResponseEntity.ok(order);
    }

    /**
     * Obtiene el historial de órdenes del usuario actual
     */
    @GetMapping("/api/users/me/orders")
    public ResponseEntity<Page<OrderResponse>> getUserOrders(
            @AuthenticationPrincipal UserPrincipal currentUser,
            Pageable pageable) {
        
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        
        Page<OrderResponse> orders = orderService.getUserOrders(currentUser.getId(), pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * Endpoint administrativo para obtener todas las órdenes
     */
    @GetMapping("/api/admin/orders")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        
        Page<OrderResponse> orders = orderService.getAllOrders(cinemaId, status, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * Cancela una orden (solo administradores)
     */
    @PostMapping("/api/admin/orders/{id}/cancel")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable UUID id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok(new ApiResponse(true, "Order cancelled successfully"));
    }

    /**
     * Crea una orden directamente en taquilla (punto de venta)
     */
    @PostMapping("/api/pos/orders")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<OrderResponse> createPosOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        OrderResponse order = orderService.createPosOrder(orderRequest, currentUser.getId());
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/orders/{id}")
                .buildAndExpand(order.getId()).toUri();
        
        return ResponseEntity.created(location).body(order);
    }

    /**
     * Endpoint para imprimir tickets en taquilla
     */
    @PostMapping("/api/pos/orders/{id}/print")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse> printTickets(@PathVariable UUID id) {
        // En una implementación real, aquí se conectaría con el sistema de impresión
        // Por ahora, solo simulamos el éxito
        return ResponseEntity.ok(new ApiResponse(true, "Tickets sent to printer"));
    }
}