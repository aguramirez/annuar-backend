package com.cinetickets.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Discount type is required")
    private String discountType; // PERCENTAGE, FIXED_AMOUNT, BUY_X_GET_Y
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0", inclusive = false, message = "Discount value must be greater than 0")
    private BigDecimal discountValue;
    
    private String code;
    
    private ZonedDateTime startDate;
    
    private ZonedDateTime endDate;
    
    private Integer usageLimit;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    
    private String appliesTo; // ALL, TICKETS, PRODUCTS, COMBOS
    
    private BigDecimal minPurchase;
}