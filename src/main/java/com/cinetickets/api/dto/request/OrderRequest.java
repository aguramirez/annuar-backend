package com.cinetickets.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    
    private UUID reservationId; // Puede ser null para órdenes directas en taquilla
    
    private String paymentMethod;
    
    private String notes;
    
    private String promotionCode;
    
    // Para órdenes directas en taquilla que no tienen reserva previa
    private UUID showId;
    
    private List<@Valid SeatSelectionRequest> seats;
    
    private List<@Valid OrderItemRequest> items;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatSelectionRequest {
        
        @NotNull(message = "Seat ID is required")
        private UUID seatId;
        
        @NotNull(message = "Ticket type ID is required")
        private UUID ticketTypeId;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        
        @NotBlank(message = "Item type is required")
        private String itemType; // PRODUCT, COMBO
        
        @NotNull(message = "Item ID is required")
        private UUID itemId;
        
        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
}