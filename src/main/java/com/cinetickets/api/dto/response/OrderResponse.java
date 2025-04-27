package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private UUID id;
    private UUID userId;
    private UUID reservationId;
    private String movieTitle;
    private ZonedDateTime showDateTime;
    private String cinemaName;
    private String roomName;
    private int numberOfSeats;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private String paymentMethod;
    private String paymentStatus;
    private String orderType;
    private String orderStatus;
    private ZonedDateTime createdAt;
}