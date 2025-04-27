package com.cinetickets.api.repository;

import com.cinetickets.api.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
    
    List<ProductCategory> findByCinemaIdAndIsActiveTrue(UUID cinemaId);
    
    Page<ProductCategory> findByCinemaId(UUID cinemaId, Pageable pageable);
}