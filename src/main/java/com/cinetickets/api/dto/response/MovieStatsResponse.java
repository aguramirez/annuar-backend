package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Respuesta de estadísticas de películas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieStatsResponse {
    private UUID movieId;
    private String title;
    private Integer attendance;
    private Integer shows;
    private BigDecimal sales;
    private Double occupancyRate;
}