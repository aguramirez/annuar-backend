package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
    
    private UUID id;
    private String rowName;
    private String number;
    private String seatType;
    private String status;
    private Boolean isAvailable;
}