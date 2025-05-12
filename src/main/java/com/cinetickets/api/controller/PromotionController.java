package com.cinetickets.api.controller;

import com.cinetickets.api.dto.request.PromotionRequest;
import com.cinetickets.api.dto.request.PromotionValidationRequest;
import com.cinetickets.api.dto.response.ApiResponse;
import com.cinetickets.api.dto.response.PromotionResponse;
import com.cinetickets.api.dto.response.PromotionValidationResponse;
import com.cinetickets.api.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    /**
     * Obtiene promociones activas
     */
    @GetMapping("/api/promotions")
    public ResponseEntity<List<PromotionResponse>> getActivePromotions() {
        List<PromotionResponse> promotions = promotionService.getActivePromotions();
        return ResponseEntity.ok(promotions);
    }

    /**
     * Valida un código promocional
     */
    @PostMapping("/api/promotions/validate")
    public ResponseEntity<PromotionValidationResponse> validatePromotion(
            @Valid @RequestBody PromotionValidationRequest request) {
        
        boolean isValid = promotionService.isValidPromotion(request.getCode(), request.getSubtotal());
        
        PromotionValidationResponse response = new PromotionValidationResponse();
        response.setCode(request.getCode());
        response.setValid(isValid);
        
        if (isValid) {
            response.setMessage("Promotion code is valid");
            response.setDiscountAmount(
                    promotionService.applyPromotion(request.getCode(), request.getSubtotal(), null)
            );
        } else {
            response.setMessage("Invalid promotion code");
            response.setDiscountAmount(null);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Crea una nueva promoción (solo administradores)
     */
    @PostMapping("/api/admin/promotions")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> createPromotion(
            @Valid @RequestBody PromotionRequest promotionRequest) {
        
        UUID promotionId = promotionService.createPromotion(promotionRequest);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/admin/promotions/{id}")
                .buildAndExpand(promotionId).toUri();
        
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Promotion created successfully"));
    }

    /**
     * Actualiza una promoción existente (solo administradores)
     */
    @PutMapping("/api/admin/promotions/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> updatePromotion(
            @PathVariable UUID id,
            @Valid @RequestBody PromotionRequest promotionRequest) {
        
        promotionService.updatePromotion(id, promotionRequest);
        
        return ResponseEntity.ok(new ApiResponse(true, "Promotion updated successfully"));
    }

    /**
     * Desactiva una promoción (solo administradores)
     */
    @DeleteMapping("/api/admin/promotions/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> deactivatePromotion(@PathVariable UUID id) {
        promotionService.deactivatePromotion(id);
        return ResponseEntity.ok(new ApiResponse(true, "Promotion deactivated successfully"));
    }
}