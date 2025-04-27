package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Respuesta de estadísticas de productos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatsResponse {
    private UUID productId;
    private String name;
    private String type;
    private Integer quantity;
    private BigDecimal sales;
}