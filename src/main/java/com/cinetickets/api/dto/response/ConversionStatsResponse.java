package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Respuesta de estadísticas de conversión
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionStatsResponse {
    private Integer reservations;
    private Integer completedOrders;
    private Double conversionRate;
    private Integer abandonment;
    private Double abandonmentRate;
    private BigDecimal lostRevenue;
}