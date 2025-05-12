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
public class PromotionResponse {
    
    private UUID id;
    private String name;
    private String description;
    private String discountType;
    private BigDecimal discountValue;
    private String code;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private Integer usageLimit;
    private Integer usageCount;
    private Boolean isActive;
    private String appliesTo;
    private BigDecimal minPurchase;
    private ZonedDateTime createdAt;
}