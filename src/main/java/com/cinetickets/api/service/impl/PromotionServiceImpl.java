package com.cinetickets.api.service.impl;

import com.cinetickets.api.dto.request.PromotionRequest;
import com.cinetickets.api.dto.response.PromotionResponse;
import com.cinetickets.api.entity.Promotion;
import com.cinetickets.api.exception.InvalidPromotionException;
import com.cinetickets.api.exception.ResourceNotFoundException;
import com.cinetickets.api.repository.PromotionRepository;
import com.cinetickets.api.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    
    // ID fijo del cine
    private final UUID defaultCinemaId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> getActivePromotions() {
        ZonedDateTime now = ZonedDateTime.now();
        List<Promotion> promotions = promotionRepository.findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
                now, now);
        
        return promotions.stream()
                .map(this::mapToPromotionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UUID createPromotion(PromotionRequest promotionRequest) {
        // Validar código único si se proporciona
        if (promotionRequest.getCode() != null && !promotionRequest.getCode().isEmpty()) {
            if (promotionRepository.existsByCode(promotionRequest.getCode())) {
                throw new InvalidPromotionException("Promotion code already exists");
            }
        }
        
        Promotion promotion = Promotion.builder()
                .id(UUID.randomUUID())
                .cinemaId(defaultCinemaId)
                .name(promotionRequest.getName())
                .description(promotionRequest.getDescription())
                .discountType(Promotion.DiscountType.valueOf(promotionRequest.getDiscountType()))
                .discountValue(promotionRequest.getDiscountValue())
                .code(promotionRequest.getCode())
                .startDate(promotionRequest.getStartDate())
                .endDate(promotionRequest.getEndDate())
                .usageLimit(promotionRequest.getUsageLimit())
                .usageCount(0)
                .isActive(promotionRequest.getIsActive())
                .appliesTo(promotionRequest.getAppliesTo() != null ? 
                        Promotion.AppliesTo.valueOf(promotionRequest.getAppliesTo()) : Promotion.AppliesTo.ALL)
                .minPurchase(promotionRequest.getMinPurchase() != null ? promotionRequest.getMinPurchase() : BigDecimal.ZERO)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        
        Promotion savedPromotion = promotionRepository.save(promotion);
        return savedPromotion.getId();
    }

    @Override
    @Transactional
    public void updatePromotion(UUID id, PromotionRequest promotionRequest) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
        
        // Validar código único si se modifica
        if (promotionRequest.getCode() != null && !promotionRequest.getCode().isEmpty() 
                && !promotionRequest.getCode().equals(promotion.getCode())) {
            if (promotionRepository.existsByCode(promotionRequest.getCode())) {
                throw new InvalidPromotionException("Promotion code already exists");
            }
        }
        
        promotion.setName(promotionRequest.getName());
        promotion.setDescription(promotionRequest.getDescription());
        promotion.setDiscountType(Promotion.DiscountType.valueOf(promotionRequest.getDiscountType()));
        promotion.setDiscountValue(promotionRequest.getDiscountValue());
        promotion.setCode(promotionRequest.getCode());
        promotion.setStartDate(promotionRequest.getStartDate());
        promotion.setEndDate(promotionRequest.getEndDate());
        promotion.setUsageLimit(promotionRequest.getUsageLimit());
        promotion.setIsActive(promotionRequest.getIsActive());
        promotion.setAppliesTo(promotionRequest.getAppliesTo() != null ? 
                Promotion.AppliesTo.valueOf(promotionRequest.getAppliesTo()) : Promotion.AppliesTo.ALL);
        promotion.setMinPurchase(promotionRequest.getMinPurchase() != null ? 
                promotionRequest.getMinPurchase() : BigDecimal.ZERO);
        promotion.setUpdatedAt(ZonedDateTime.now());
        
        promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public void deactivatePromotion(UUID id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
        
        promotion.setIsActive(false);
        promotion.setUpdatedAt(ZonedDateTime.now());
        
        promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public BigDecimal applyPromotion(String code, BigDecimal subtotal, UUID userId) {
        if (code == null || code.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        Promotion promotion = promotionRepository.findByCode(code)
                .orElseThrow(() -> new InvalidPromotionException("Invalid promotion code"));
        
        // Validar promoción
        validatePromotion(promotion, subtotal);
        
        // Calcular descuento
        BigDecimal discount = calculateDiscount(promotion, subtotal);
        
        // Incrementar contador de uso
        promotion.setUsageCount(promotion.getUsageCount() + 1);
        promotionRepository.save(promotion);
        
        return discount;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidPromotion(String code, BigDecimal subtotal) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        
        try {
            Promotion promotion = promotionRepository.findByCode(code)
                    .orElseThrow(() -> new InvalidPromotionException("Invalid promotion code"));
            
            validatePromotion(promotion, subtotal);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Valida que una promoción sea aplicable
     */
    private void validatePromotion(Promotion promotion, BigDecimal subtotal) {
        // Validar que esté activa
        if (!promotion.getIsActive()) {
            throw new InvalidPromotionException("Promotion is not active");
        }
        
        // Validar fechas
        ZonedDateTime now = ZonedDateTime.now();
        if (promotion.getStartDate() != null && now.isBefore(promotion.getStartDate())) {
            throw new InvalidPromotionException("Promotion has not started yet");
        }
        if (promotion.getEndDate() != null && now.isAfter(promotion.getEndDate())) {
            throw new InvalidPromotionException("Promotion has expired");
        }
        
        // Validar límite de uso
        if (promotion.getUsageLimit() != null && promotion.getUsageCount() >= promotion.getUsageLimit()) {
            throw new InvalidPromotionException("Promotion usage limit has been reached");
        }
        
        // Validar compra mínima
        if (subtotal.compareTo(promotion.getMinPurchase()) < 0) {
            throw new InvalidPromotionException("Minimum purchase amount not met");
        }
    }
    
    /**
     * Calcula el descuento según el tipo de promoción
     */
    private BigDecimal calculateDiscount(Promotion promotion, BigDecimal subtotal) {
        BigDecimal discount;
        
        switch (promotion.getDiscountType()) {
            case PERCENTAGE:
                // Ejemplo: 10% de descuento
                discount = subtotal.multiply(promotion.getDiscountValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                break;
            case FIXED_AMOUNT:
                // Ejemplo: $500 de descuento
                discount = promotion.getDiscountValue();
                // No aplicar más descuento que el subtotal
                if (discount.compareTo(subtotal) > 0) {
                    discount = subtotal;
                }
                break;
            case BUY_X_GET_Y:
                // No implementado en esta versión básica
                discount = BigDecimal.ZERO;
                break;
            default:
                discount = BigDecimal.ZERO;
        }
        
        return discount;
    }
    
    /**
     * Mapea una entidad Promotion a un DTO PromotionResponse
     */
    private PromotionResponse mapToPromotionResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType().name())
                .discountValue(promotion.getDiscountValue())
                .code(promotion.getCode())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .usageLimit(promotion.getUsageLimit())
                .usageCount(promotion.getUsageCount())
                .isActive(promotion.getIsActive())
                .appliesTo(promotion.getAppliesTo().name())
                .minPurchase(promotion.getMinPurchase())
                .createdAt(promotion.getCreatedAt())
                .build();
    }

    @Override
    public List<PromotionResponse> getActivePromotions(UUID defaultCinemaId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getActivePromotions'");
    }
}