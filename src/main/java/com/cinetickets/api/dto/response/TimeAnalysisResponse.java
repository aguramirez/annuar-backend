package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

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