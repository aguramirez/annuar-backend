package com.cinetickets.api.service;

import com.cinetickets.api.dto.response.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StatsService {
    
    /**
     * Obtiene estadísticas de ventas para un cine en un rango de fechas
     * @param cinemaId ID del cine
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Estadísticas de ventas
     */
    SalesStatsResponse getSalesStats(LocalDate startDate, LocalDate endDate);
    
    /**
     * Obtiene estadísticas de asistencia para un cine en un rango de fechas
     * @param cinemaId ID del cine
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Estadísticas de asistencia
     */
    AttendanceStatsResponse getAttendanceStats(LocalDate startDate, LocalDate endDate);
    
    /**
     * Obtiene estadísticas de las películas más vistas en un rango de fechas
     * @param cinemaId ID del cine
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @param limit Límite de resultados
     * @return Lista de estadísticas de películas
     */
    List<MovieStatsResponse> getTopMoviesStats(LocalDate startDate, LocalDate endDate, int limit);
    
    /**
     * Obtiene estadísticas de los productos más vendidos en un rango de fechas
     * @param cinemaId ID del cine
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @param limit Límite de resultados
     * @return Lista de estadísticas de productos
     */
    List<ProductStatsResponse> getTopProductsStats(LocalDate startDate, LocalDate endDate, int limit);
    
    /**
     * Obtiene estadísticas de conversión de ventas en un rango de fechas
     * @param cinemaId ID del cine
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Estadísticas de conversión
     */
    ConversionStatsResponse getConversionStats(LocalDate startDate, LocalDate endDate);
    
    /**
     * Obtiene estadísticas de días y horarios más concurridos en un rango de fechas
     * @param cinemaId ID del cine
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Análisis de tiempos
     */
    TimeAnalysisResponse getTimeAnalysis(LocalDate startDate, LocalDate endDate);
    
    /**
     * Obtiene estadísticas de los usuarios más frecuentes en un rango de fechas
     * @param cinemaId ID del cine
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @param limit Límite de resultados
     * @return Lista de estadísticas de usuarios
     */
    List<UserStatsResponse> getTopUserStats(LocalDate startDate, LocalDate endDate, int limit);
    
    /**
     * Obtiene datos para exportar en un rango de fechas
     * @param cinemaId ID del cine
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @param type Tipo de datos a exportar
     * @return Lista de datos para exportar
     */
    List<ExportDataResponse> getExportData(LocalDate startDate, LocalDate endDate, String type);
}