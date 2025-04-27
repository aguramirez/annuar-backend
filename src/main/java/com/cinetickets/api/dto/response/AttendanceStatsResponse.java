package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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