package com.cinetickets.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    private String imageUrl;
    
    private Integer stock;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    
    /**
     * DTO para solicitudes de combos
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComboRequest {
        
        @NotBlank(message = "Name is required")
        private String name;
        
        private String description;
        
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0", inclusive = false, message = "Price must be greater than 0")
        private BigDecimal price;
        
        private String imageUrl;
        
        @NotNull(message = "Active status is required")
        private Boolean isActive;
        
        @NotNull(message = "Combo items are required")
        private List<@Valid ComboItemRequest> items;
    }
    
    /**
     * DTO para ítems de combos
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComboItemRequest {
        
        @NotNull(message = "Product ID is required")
        private UUID productId;
        
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
    
    /**
     * DTO para categorías de productos
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryRequest {
        
        @NotBlank(message = "Name is required")
        private String name;
        
        private String description;
        
        private String imageUrl;
        
        private Integer displayOrder;
        
        @NotNull(message = "Active status is required")
        private Boolean isActive;
    }
}