package com.cinetickets.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketValidationRequest {
    
    @NotBlank(message = "QR content is required")
    private String qrContent;
    
    private String showId;
}