package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsResponse {
    
    private UUID id;
    private UUID userId;
    private String userFullName;
    private String userEmail;
    private ShowResponse showDetails;
    private List<ReservedSeatResponse> reservedSeats;
    private List<OrderItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private String paymentMethod;
    private String paymentStatus;
    private String orderType;
    private String orderStatus;
    private String qrCode;
    private ZonedDateTime createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservedSeatResponse {
        private UUID id;
        private String rowName;
        private String number;
        private String seatType;
        private String ticketType;
        private BigDecimal price;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private UUID id;
        private String itemType;
        private String name;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}