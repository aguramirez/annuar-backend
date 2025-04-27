package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Respuesta de estadísticas de ventas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesStatsResponse {
    private BigDecimal totalSales;
    private BigDecimal ticketSales;
    private BigDecimal productSales;
    private BigDecimal averageTicketPrice;
    private BigDecimal averageTransactionValue;
    private Integer totalTransactions;
    private List<DailySalesData> dailySales;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailySalesData {
        private LocalDate date;
        private BigDecimal totalSales;
        private Integer transactions;
    }
}