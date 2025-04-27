package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

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