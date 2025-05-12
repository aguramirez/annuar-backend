package com.cinetickets.api.controller;

import com.cinetickets.api.dto.response.*;
import com.cinetickets.api.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /**
     * Obtiene estadísticas de ventas
     */
    @GetMapping("/sales")
    public ResponseEntity<SalesStatsResponse> getSalesStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        SalesStatsResponse stats = statsService.getSalesStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene estadísticas de asistencia
     */
    @GetMapping("/attendance")
    public ResponseEntity<AttendanceStatsResponse> getAttendanceStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        AttendanceStatsResponse stats = statsService.getAttendanceStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene estadísticas de películas más vistas
     */
    @GetMapping("/movies")
    public ResponseEntity<List<MovieStatsResponse>> getMovieStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<MovieStatsResponse> stats = statsService.getTopMoviesStats(startDate, endDate, limit);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene estadísticas de productos más vendidos
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductStatsResponse>> getProductStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ProductStatsResponse> stats = statsService.getTopProductsStats(startDate, endDate, limit);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene estadísticas de conversión de ventas
     */
    @GetMapping("/conversion")
    public ResponseEntity<ConversionStatsResponse> getConversionStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ConversionStatsResponse stats = statsService.getConversionStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene estadísticas de días y horarios más concurridos
     */
    @GetMapping("/time-analysis")
    public ResponseEntity<TimeAnalysisResponse> getTimeAnalysis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        TimeAnalysisResponse stats = statsService.getTimeAnalysis(startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Obtiene estadísticas de usuarios frecuentes
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserStatsResponse>> getTopUsers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<UserStatsResponse> stats = statsService.getTopUserStats(startDate, endDate, limit);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Obtiene datos para exportar
     */
    @GetMapping("/export")
    public ResponseEntity<List<ExportDataResponse>> getExportData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String type) {
        
        List<ExportDataResponse> data = statsService.getExportData(startDate, endDate, type);
        return ResponseEntity.ok(data);
    }
}