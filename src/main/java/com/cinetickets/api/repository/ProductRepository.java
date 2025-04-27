package com.cinetickets.api.repository;

import com.cinetickets.api.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    List<Product> findByCategoryCinemaIdAndIsActiveTrue(UUID cinemaId);
    
    List<Product> findByCategoryIdAndIsActiveTrue(UUID categoryId);
    
    Page<Product> findByCategoryCinemaId(UUID cinemaId, Pageable pageable);
}