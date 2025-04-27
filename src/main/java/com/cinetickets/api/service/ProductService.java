package com.cinetickets.api.service;

import com.cinetickets.api.dto.request.ProductRequest;
import com.cinetickets.api.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    
    /**
     * Obtiene todos los productos disponibles para un cine
     * @param cinemaId ID del cine
     * @return Lista de productos
     */
    List<ProductResponse> getAvailableProducts(UUID cinemaId);
    
    /**
     * Obtiene productos por categoría para un cine
     * @param cinemaId ID del cine
     * @param categoryId ID de la categoría
     * @return Lista de productos
     */
    List<ProductResponse> getProductsByCategory(UUID cinemaId, UUID categoryId);
    
    /**
     * Obtiene todas las categorías de productos para un cine
     * @param cinemaId ID del cine
     * @return Lista de categorías
     */
    List<ProductResponse.CategoryResponse> getProductCategories(UUID cinemaId);
    
    /**
     * Obtiene todos los combos disponibles para un cine
     * @param cinemaId ID del cine
     * @return Lista de combos
     */
    List<ProductResponse.ComboResponse> getAvailableCombos(UUID cinemaId);
    
    /**
     * Obtiene todos los productos de un cine (admin)
     * @param cinemaId ID del cine
     * @param pageable Configuración de paginación
     * @return Página de productos
     */
    Page<ProductResponse> getAllProducts(UUID cinemaId, Pageable pageable);
    
    /**
     * Crea un nuevo producto
     * @param productRequest Datos del producto
     * @return ID del producto creado
     */
    UUID createProduct(ProductRequest productRequest);
    
    /**
     * Actualiza un producto existente
     * @param id ID del producto
     * @param productRequest Nuevos datos del producto
     */
    void updateProduct(UUID id, ProductRequest productRequest);
    
    /**
     * Elimina un producto
     * @param id ID del producto
     */
    void deleteProduct(UUID id);
    
    /**
     * Obtiene todos los combos de un cine (admin)
     * @param cinemaId ID del cine
     * @param pageable Configuración de paginación
     * @return Página de combos
     */
    Page<ProductResponse.ComboResponse> getAllCombos(UUID cinemaId, Pageable pageable);
    
    /**
     * Crea un nuevo combo
     * @param comboRequest Datos del combo
     * @return ID del combo creado
     */
    UUID createCombo(ProductRequest.ComboRequest comboRequest);
    
    /**
     * Actualiza un combo existente
     * @param id ID del combo
     * @param comboRequest Nuevos datos del combo
     */
    void updateCombo(UUID id, ProductRequest.ComboRequest comboRequest);
    
    /**
     * Elimina un combo
     * @param id ID del combo
     */
    void deleteCombo(UUID id);
    
    /**
     * Obtiene todas las categorías de productos de un cine (admin)
     * @param cinemaId ID del cine
     * @param pageable Configuración de paginación
     * @return Página de categorías
     */
    Page<ProductResponse.CategoryResponse> getAllProductCategories(UUID cinemaId, Pageable pageable);
    
    /**
     * Crea una nueva categoría de productos
     * @param categoryRequest Datos de la categoría
     * @return ID de la categoría creada
     */
    UUID createProductCategory(ProductRequest.CategoryRequest categoryRequest);
    
    /**
     * Actualiza una categoría de productos existente
     * @param id ID de la categoría
     * @param categoryRequest Nuevos datos de la categoría
     */
    void updateProductCategory(UUID id, ProductRequest.CategoryRequest categoryRequest);
    
    /**
     * Elimina una categoría de productos
     * @param id ID de la categoría
     */
    void deleteProductCategory(UUID id);
}