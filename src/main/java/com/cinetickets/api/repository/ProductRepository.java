package com.cinetickets.api.repository;

import com.cinetickets.api.entity.Product;
import com.cinetickets.api.entity.Promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByCategoryIdAndIsActiveTrue(UUID categoryId);
    List<Promotion> findByIsActiveTrue();
    List<Promotion> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
            ZonedDateTime now, ZonedDateTime now2);
}