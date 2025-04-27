package com.cinetickets.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionValidationRequest {
    
    @NotBlank(message = "Promotion code is required")
    private String code;
    
    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0", inclusive = true, message = "Subtotal must be non-negative")
    private BigDecimal subtotal;
}