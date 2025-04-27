package com.cinetickets.api.dto.request;

import jakarta.validation.Valid;
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
public class ReservationRequest {
    
    @NotNull(message = "Show ID is required")
    private UUID showId;
    
    @NotEmpty(message = "At least one seat must be selected")
    private List<@Valid SeatSelectionRequest> seats;
    
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
}