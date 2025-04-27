package com.cinetickets.api.service;

import com.cinetickets.api.dto.request.PromotionRequest;
import com.cinetickets.api.dto.response.PromotionResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PromotionService {
    
    /**
     * Obtiene todas las promociones activas para un cine
     * @param cinemaId ID del cine
     * @return Lista de promociones
     */
    List<PromotionResponse> getActivePromotions(UUID cinemaId);
    
    /**
     * Crea una nueva promoción
     * @param promotionRequest Datos de la promoción
     * @return ID de la promoción creada
     */
    UUID createPromotion(PromotionRequest promotionRequest);
    
    /**
     * Actualiza una promoción existente
     * @param id ID de la promoción
     * @param promotionRequest Nuevos datos de la promoción
     */
    void updatePromotion(UUID id, PromotionRequest promotionRequest);
    
    /**
     * Desactiva una promoción
     * @param id ID de la promoción
     */
    void deactivatePromotion(UUID id);
    
    /**
     * Valida y aplica un código promocional
     * @param code Código promocional
     * @param subtotal Monto subtotal
     * @param userId ID del usuario (opcional)
     * @return Monto del descuento aplicado
     */
    BigDecimal applyPromotion(String code, BigDecimal subtotal, UUID userId);
    
    /**
     * Verifica si un código promocional es válido
     * @param code Código promocional
     * @param subtotal Monto subtotal
     * @return true si es válido, false si no
     */
    boolean isValidPromotion(String code, BigDecimal subtotal);
}