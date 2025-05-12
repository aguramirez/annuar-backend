package com.cinetickets.api.controller;

import com.cinetickets.api.dto.request.ProductRequest;
import com.cinetickets.api.dto.response.ApiResponse;
import com.cinetickets.api.dto.response.ProductResponse;
import com.cinetickets.api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Obtiene todos los productos disponibles
     */
    @GetMapping("/api/products")
    public ResponseEntity<List<ProductResponse>> getAvailableProducts(
            @RequestParam(required = false) UUID categoryId) {
        
        List<ProductResponse> products;
        if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
        } else {
            products = productService.getAvailableProducts();
        }
        
        return ResponseEntity.ok(products);
    }

    /**
     * Obtiene las categorías de productos
     */
    @GetMapping("/api/products/categories")
    public ResponseEntity<List<ProductResponse.CategoryResponse>> getProductCategories() {
        List<ProductResponse.CategoryResponse> categories = productService.getProductCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Obtiene los combos disponibles
     */
    @GetMapping("/api/combos")
    public ResponseEntity<List<ProductResponse.ComboResponse>> getAvailableCombos() {
        List<ProductResponse.ComboResponse> combos = productService.getAvailableCombos();
        return ResponseEntity.ok(combos);
    }

    /**
     * Endpoints administrativos para gestión de productos
     */
    @GetMapping("/api/admin/products")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/api/admin/products")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> createProduct(
            @Valid @RequestBody ProductRequest productRequest) {
        
        UUID productId = productService.createProduct(productRequest);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/admin/products/{id}")
                .buildAndExpand(productId).toUri();
        
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Product created successfully"));
    }

    @PutMapping("/api/admin/products/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest productRequest) {
        
        productService.updateProduct(id, productRequest);
        
        return ResponseEntity.ok(new ApiResponse(true, "Product updated successfully"));
    }

    @DeleteMapping("/api/admin/products/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse(true, "Product deleted successfully"));
    }

    /**
     * Endpoints administrativos para gestión de combos
     */
    @GetMapping("/api/admin/combos")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<ProductResponse.ComboResponse>> getAllCombos(Pageable pageable) {
        Page<ProductResponse.ComboResponse> combos = productService.getAllCombos(pageable);
        return ResponseEntity.ok(combos);
    }

    @PostMapping("/api/admin/combos")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> createCombo(
            @Valid @RequestBody ProductRequest.ComboRequest comboRequest) {
        
        UUID comboId = productService.createCombo(comboRequest);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/admin/combos/{id}")
                .buildAndExpand(comboId).toUri();
        
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Combo created successfully"));
    }

    @PutMapping("/api/admin/combos/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> updateCombo(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest.ComboRequest comboRequest) {
        
        productService.updateCombo(id, comboRequest);
        
        return ResponseEntity.ok(new ApiResponse(true, "Combo updated successfully"));
    }

    @DeleteMapping("/api/admin/combos/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCombo(@PathVariable UUID id) {
        productService.deleteCombo(id);
        return ResponseEntity.ok(new ApiResponse(true, "Combo deleted successfully"));
    }
    
    /**
     * Endpoints administrativos para gestión de categorías de productos
     */
    @GetMapping("/api/admin/product-categories")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<ProductResponse.CategoryResponse>> getAllProductCategories(Pageable pageable) {
        Page<ProductResponse.CategoryResponse> categories = productService.getAllProductCategories(pageable);
        return ResponseEntity.ok(categories);
    }
    
    @PostMapping("/api/admin/product-categories")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> createProductCategory(
            @Valid @RequestBody ProductRequest.CategoryRequest categoryRequest) {
        
        UUID categoryId = productService.createProductCategory(categoryRequest);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/admin/product-categories/{id}")
                .buildAndExpand(categoryId).toUri();
        
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Product category created successfully"));
    }
    
    @PutMapping("/api/admin/product-categories/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> updateProductCategory(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest.CategoryRequest categoryRequest) {
        
        productService.updateProductCategory(id, categoryRequest);
        
        return ResponseEntity.ok(new ApiResponse(true, "Product category updated successfully"));
    }
    
    @DeleteMapping("/api/admin/product-categories/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> deleteProductCategory(@PathVariable UUID id) {
        productService.deleteProductCategory(id);
        return ResponseEntity.ok(new ApiResponse(true, "Product category deleted successfully"));
    }
}