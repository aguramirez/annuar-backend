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
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/stats")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /**
     * Obtiene estadísticas de ventas para un cine
     */
    @GetMapping("/sales")
    public ResponseEntity<SalesStatsResponse> getSalesStats(
            @RequestParam UUID cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        SalesStatsResponse stats = statsService.getSalesStats(cinemaId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene estadísticas de asistencia para un cine
     */
    @GetMapping("/attendance")
    public ResponseEntity<AttendanceStatsResponse> getAttendanceStats(
            @RequestParam UUID cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        AttendanceStatsResponse stats = statsService.getAttendanceStats(cinemaId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene estadísticas de películas más vistas
     */
    @GetMapping("/movies")
    public ResponseEntity<List<MovieStatsResponse>> getMovieStats(
            @RequestParam UUID cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<MovieStatsResponse> stats = statsService.getTopMoviesStats(cinemaId, startDate, endDate, limit);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene estadísticas de productos más vendidos
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductStatsResponse>> getProductStats(
            @RequestParam UUID cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ProductStatsResponse> stats = statsService.getTopProductsStats(cinemaId, startDate, endDate, limit);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene estadísticas de conversión de ventas
     */
    @GetMapping("/conversion")
    public ResponseEntity<ConversionStatsResponse> getConversionStats(
            @RequestParam UUID cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ConversionStatsResponse stats = statsService.getConversionStats(cinemaId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene estadísticas de días y horarios más concurridos
     */
    @GetMapping("/time-analysis")
    public ResponseEntity<TimeAnalysisResponse> getTimeAnalysis(
            @RequestParam UUID cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        TimeAnalysisResponse stats = statsService.getTimeAnalysis(cinemaId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Obtiene estadísticas de usuarios frecuentes
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserStatsResponse>> getTopUsers(
            @RequestParam UUID cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<UserStatsResponse> stats = statsService.getTopUserStats(cinemaId, startDate, endDate, limit);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Obtiene datos para exportar
     */
    @GetMapping("/export")
    public ResponseEntity<List<ExportDataResponse>> getExportData(
            @RequestParam UUID cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String type) {
        
        List<ExportDataResponse> data = statsService.getExportData(cinemaId, startDate, endDate, type);
        return ResponseEntity.ok(data);
    }
}