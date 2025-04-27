package com.cinetickets.api.service.impl;

import com.cinetickets.api.dto.request.ProductRequest;
import com.cinetickets.api.dto.response.ProductResponse;
import com.cinetickets.api.entity.Cinema;
import com.cinetickets.api.entity.Combo;
import com.cinetickets.api.entity.ComboItem;
import com.cinetickets.api.entity.Product;
import com.cinetickets.api.entity.ProductCategory;
import com.cinetickets.api.exception.ResourceNotFoundException;
import com.cinetickets.api.repository.CinemaRepository;
import com.cinetickets.api.repository.ComboRepository;
import com.cinetickets.api.repository.ProductCategoryRepository;
import com.cinetickets.api.repository.ProductRepository;
import com.cinetickets.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ComboRepository comboRepository;
    private final CinemaRepository cinemaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAvailableProducts(UUID cinemaId) {
        List<Product> products = productRepository.findByCategoryCinemaIdAndIsActiveTrue(cinemaId);
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(UUID cinemaId, UUID categoryId) {
        List<Product> products = productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse.CategoryResponse> getProductCategories(UUID cinemaId) {
        List<ProductCategory> categories = categoryRepository.findByCinemaIdAndIsActiveTrue(cinemaId);
        return categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse.ComboResponse> getAvailableCombos(UUID cinemaId) {
        List<Combo> combos = comboRepository.findByCinemaIdAndIsActiveTrue(cinemaId);
        return combos.stream()
                .map(this::mapToComboResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(UUID cinemaId, Pageable pageable) {
        Page<Product> products = productRepository.findByCategoryCinemaId(cinemaId, pageable);
        return products.map(this::mapToProductResponse);
    }

    @Override
    @Transactional
    public UUID createProduct(ProductRequest productRequest) {
        ProductCategory category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "id", productRequest.getCategoryId()));
        
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .category(category)
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .imageUrl(productRequest.getImageUrl())
                .stock(productRequest.getStock())
                .isActive(productRequest.getIsActive())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    @Override
    @Transactional
    public void updateProduct(UUID id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        ProductCategory category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "id", productRequest.getCategoryId()));
        
        product.setCategory(category);
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setImageUrl(productRequest.getImageUrl());
        product.setStock(productRequest.getStock());
        product.setIsActive(productRequest.getIsActive());
        product.setUpdatedAt(ZonedDateTime.now());
        
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Verificar si el producto está siendo usado en combos
        if (!product.getComboItems().isEmpty()) {
            // Soft delete
            product.setIsActive(false);
            product.setUpdatedAt(ZonedDateTime.now());
            productRepository.save(product);
        } else {
            // Hard delete
            productRepository.delete(product);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse.ComboResponse> getAllCombos(UUID cinemaId, Pageable pageable) {
        Page<Combo> combos = comboRepository.findByCinemaId(cinemaId, pageable);
        return combos.map(this::mapToComboResponse);
    }

    @Override
    @Transactional
    public UUID createCombo(ProductRequest.ComboRequest comboRequest) {
        Cinema cinema = cinemaRepository.findById(comboRequest.getCinemaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cinema", "id", comboRequest.getCinemaId()));
        
        Combo combo = Combo.builder()
                .id(UUID.randomUUID())
                .cinema(cinema)
                .name(comboRequest.getName())
                .description(comboRequest.getDescription())
                .price(comboRequest.getPrice())
                .imageUrl(comboRequest.getImageUrl())
                .isActive(comboRequest.getIsActive())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        
        List<ComboItem> comboItems = new ArrayList<>();
        for (ProductRequest.ComboItemRequest itemRequest : comboRequest.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemRequest.getProductId()));
            
            ComboItem comboItem = ComboItem.builder()
                    .id(UUID.randomUUID())
                    .combo(combo)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build();
            
            comboItems.add(comboItem);
        }
        
        combo.setItems(comboItems);
        Combo savedCombo = comboRepository.save(combo);
        
        return savedCombo.getId();
    }

    @Override
    @Transactional
    public void updateCombo(UUID id, ProductRequest.ComboRequest comboRequest) {
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Combo", "id", id));
        
        Cinema cinema = cinemaRepository.findById(comboRequest.getCinemaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cinema", "id", comboRequest.getCinemaId()));
        
        combo.setCinema(cinema);
        combo.setName(comboRequest.getName());
        combo.setDescription(comboRequest.getDescription());
        combo.setPrice(comboRequest.getPrice());
        combo.setImageUrl(comboRequest.getImageUrl());
        combo.setIsActive(comboRequest.getIsActive());
        combo.setUpdatedAt(ZonedDateTime.now());
        
        // Eliminar items antiguos
        combo.getItems().clear();
        
        // Agregar nuevos items
        List<ComboItem> comboItems = new ArrayList<>();
        for (ProductRequest.ComboItemRequest itemRequest : comboRequest.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemRequest.getProductId()));
            
            ComboItem comboItem = ComboItem.builder()
                    .id(UUID.randomUUID())
                    .combo(combo)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build();
            
            comboItems.add(comboItem);
        }
        
        combo.setItems(comboItems);
        comboRepository.save(combo);
    }

    @Override
    @Transactional
    public void deleteCombo(UUID id) {
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Combo", "id", id));
        
        comboRepository.delete(combo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse.CategoryResponse> getAllProductCategories(UUID cinemaId, Pageable pageable) {
        Page<ProductCategory> categories = categoryRepository.findByCinemaId(cinemaId, pageable);
        return categories.map(this::mapToCategoryResponse);
    }

    @Override
    @Transactional
    public UUID createProductCategory(ProductRequest.CategoryRequest categoryRequest) {
        Cinema cinema = cinemaRepository.findById(categoryRequest.getCinemaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cinema", "id", categoryRequest.getCinemaId()));
        
        ProductCategory category = ProductCategory.builder()
                .id(UUID.randomUUID())
                .cinema(cinema)
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .imageUrl(categoryRequest.getImageUrl())
                .displayOrder(categoryRequest.getDisplayOrder())
                .isActive(categoryRequest.getIsActive())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        
        ProductCategory savedCategory = categoryRepository.save(category);
        return savedCategory.getId();
    }

    @Override
    @Transactional
    public void updateProductCategory(UUID id, ProductRequest.CategoryRequest categoryRequest) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "id", id));
        
        Cinema cinema = cinemaRepository.findById(categoryRequest.getCinemaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cinema", "id", categoryRequest.getCinemaId()));
        
        category.setCinema(cinema);
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setImageUrl(categoryRequest.getImageUrl());
        category.setDisplayOrder(categoryRequest.getDisplayOrder());
        category.setIsActive(categoryRequest.getIsActive());
        category.setUpdatedAt(ZonedDateTime.now());
        
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteProductCategory(UUID id) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "id", id));
        
        // Verificar si la categoría tiene productos
        if (!category.getProducts().isEmpty()) {
            // Soft delete
            category.setIsActive(false);
            category.setUpdatedAt(ZonedDateTime.now());
            categoryRepository.save(category);
        } else {
            // Hard delete
            categoryRepository.delete(category);
        }
    }
    
    /**
     * Mapea una entidad Product a un DTO ProductResponse
     */
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .stock(product.getStock())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
    
    /**
     * Mapea una entidad Combo a un DTO ComboResponse
     */
    private ProductResponse.ComboResponse mapToComboResponse(Combo combo) {
        List<ProductResponse.ComboItemResponse> items = combo.getItems().stream()
                .map(item -> ProductResponse.ComboItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productDescription(item.getProduct().getDescription())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());
        
        return ProductResponse.ComboResponse.builder()
                .id(combo.getId())
                .cinemaId(combo.getCinema().getId())
                .name(combo.getName())
                .description(combo.getDescription())
                .price(combo.getPrice())
                .imageUrl(combo.getImageUrl())
                .isActive(combo.getIsActive())
                .createdAt(combo.getCreatedAt())
                .items(items)
                .build();
    }
    
    /**
     * Mapea una entidad ProductCategory a un DTO CategoryResponse
     */
    private ProductResponse.CategoryResponse mapToCategoryResponse(ProductCategory category) {
        return ProductResponse.CategoryResponse.builder()
                .id(category.getId())
                .cinemaId(category.getCinema().getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .displayOrder(category.getDisplayOrder())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .build();
    }
}