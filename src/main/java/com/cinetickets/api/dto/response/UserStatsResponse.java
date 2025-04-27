package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Respuesta de estadísticas de usuarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {
    private UUID userId;
    private String name;
    private String email;
    private Integer visits;
    private BigDecimal totalSpent;
    private Integer loyaltyPoints;
    private LocalDate lastVisit;
}