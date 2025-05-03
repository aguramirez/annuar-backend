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
public class ReservationResponse {
    
    private UUID id;
    private UUID userId;
    private ShowResponse show;
    private List<ReservedSeatDTO> seats;
    private ZonedDateTime expiresAt;
    private String status;
    private ZonedDateTime createdAt;
    private BigDecimal totalAmount;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservedSeatDTO {
        private UUID id;
        private UUID seatId;
        private String rowName;
        private String number;
        private String ticketType;
        private BigDecimal price;
    }
}