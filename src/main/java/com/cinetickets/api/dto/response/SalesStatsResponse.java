package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

/**
 * Respuesta de estadísticas de asistencia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatsResponse {
    private Integer totalAttendance;
    private Double averageOccupancyRate;
    private Integer totalShows;
    private Map<String, Integer> attendanceByRoom;
    private List<DailyAttendanceData> dailyAttendance;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyAttendanceData {
        private LocalDate date;
        private Integer attendance;
        private Integer shows;
        private Double occupancyRate;
    }
}

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

/**
 * Respuesta de análisis de tiempos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeAnalysisResponse {
    private Map<DayOfWeek, Integer> attendanceByDayOfWeek;
    private Map<Integer, Integer> attendanceByHour;
    private List<PeakTimeData> peakTimes;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeakTimeData {
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private Integer attendance;
        private Double occupancyRate;
    }
}

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

/**
 * Respuesta genérica para exportación de datos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportDataResponse {
    private Map<String, Object> data;
}